package org.pranata.roomwordssample;

import android.content.Context;
import android.os.AsyncTask;

import androidx.annotation.NonNull;
import androidx.room.Database;
import androidx.room.Room;
import androidx.room.RoomDatabase;
import androidx.sqlite.db.SupportSQLiteDatabase;

@Database(entities = {Word.class}, version = 1, exportSchema = false)
public abstract class WordRoomDatabase extends RoomDatabase {
    public abstract WordDao wordDao();
    private static WordRoomDatabase INSTANCE;

    public static WordRoomDatabase getDatabase(final Context context) {
        if (INSTANCE == null) {
            synchronized (WordRoomDatabase.class) {
                if (INSTANCE == null) {
                    INSTANCE = Room.databaseBuilder(context.getApplicationContext(),
                            WordRoomDatabase.class, "word_database").
                            addCallback(sRoomDatabaseCallback).
                            fallbackToDestructiveMigration().
                            build();
                }
            }
        }
        return INSTANCE;
    }

    private static RoomDatabase.Callback sRoomDatabaseCallback = new RoomDatabase.Callback(){
        @Override
        public void onOpen(@NonNull SupportSQLiteDatabase db) {
            super.onOpen(db);
            new populateDbAsync(INSTANCE).execute();
        }


    };

    private static class populateDbAsync extends AsyncTask<Void, Void, Void>{

        private final WordDao mDao;
        String words[] = {"Alpha", "Beta", "Gamma"};

        @Override
        protected Void doInBackground(Void... voids) {

            //Only initialise database if it is empty
            if(mDao.getAnyWord().length < 1){
                for (int i = 0; i <= words.length - 1; i++) {
                    Word word = new Word(words[i]);
                    mDao.insert(word);
                }
            }

            return null;
        }

        public populateDbAsync(WordRoomDatabase instance) {
            mDao = instance.wordDao();
        }
    }


}
