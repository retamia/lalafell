package org.retamia.lalafell.record.utils;

import android.content.Context;
import android.content.res.Resources;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;

public class TextResourceReader {

    public static String readTextFileFromResource(Context context, int resourceId) throws RuntimeException {
        StringBuilder body = new StringBuilder();

        try {
            InputStream inputStream = context.getResources().openRawResource(resourceId);
            InputStreamReader inputStreamReader = new InputStreamReader(inputStream);
            BufferedReader bufferedReader = new BufferedReader(inputStreamReader);

            String nextLine = bufferedReader.readLine();

            while ((nextLine) != null) {
                body.append(nextLine);
                body.append('\n');
                nextLine = bufferedReader.readLine();
            }

        } catch (IOException ex) {
            throw new RuntimeException("cloud not open resource: $resourceId", ex);
        } catch (Resources.NotFoundException notFoundEx) {
            throw new RuntimeException("resource not found: $resourceId", notFoundEx);
        }

        return body.toString();
    }
}
