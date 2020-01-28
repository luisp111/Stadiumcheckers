package edu.up.cs301.game.GameFramework.utilities;

import android.content.Context;

import java.io.BufferedReader;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;


/**
 * Helper-class for showing dialog boxes
 * @author Eric Imperio
 * @version January 2020
 *
 */
public class Saving {
        private static final String TAG = "Saving";

        public static final String SEPARATOR = ":-:";
        public static final String ARRAY_SEPARATOR = ":=:";
        public static final String SECOND_ARRAY_SEPARATOR = ":~:";
        public static final String OBJECT_SEPARATOR = ":_:";
        public static final String SUB_OBJECT_SEPARATOR = ":`:";
        public static final String SECOND_SUB_OBJECT_SEPARATOR = ":--:";

        /**
         * writeToFile, this saves a given string to a file. Designed to save gameStates
         * @param data
         *              The String representation of the gameState to save
         * @param fileName
         *              This Name of the file to write to
         * @param context
         *              The current context. (Must not be null).
         */
        public static void writeToFile(String data, String fileName, Context context) {
                try {
                        OutputStreamWriter outputStreamWriter = new OutputStreamWriter(context.openFileOutput(fileName, Context.MODE_PRIVATE));
                        outputStreamWriter.write(data);
                        outputStreamWriter.close();
                }
                catch (IOException e) {
                        Logger.log(TAG, "File write failed" + e.toString(), Logger.ERROR);
                }
        }

        /**
         * readFromFile, designed to read a file and return the string representation of a gameState
         *
         * @param fileName
         *              The name of the file to read
         * @param context
         *              The current context. (Must not be null).
         * @return String represantion of the gameState to load
         */
        public static String readFromFile(String fileName, Context context) {

                String ret = "";

                try {
                        InputStream inputStream = context.openFileInput(fileName);

                        if ( inputStream != null ) {
                                InputStreamReader inputStreamReader = new InputStreamReader(inputStream);
                                BufferedReader bufferedReader = new BufferedReader(inputStreamReader);
                                String receiveString = "";
                                StringBuilder stringBuilder = new StringBuilder();

                                while ( (receiveString = bufferedReader.readLine()) != null ) {
                                        stringBuilder.append(receiveString);
                                }

                                inputStream.close();
                                ret = stringBuilder.toString();
                        }
                }
                catch (FileNotFoundException e) {
                        Logger.log(TAG, "File not Found: " + e.toString() , Logger.ERROR);
                } catch (IOException e) {
                        Logger.log(TAG, "Can not read file: " + e.toString());
                }

                return ret;
        }
}
