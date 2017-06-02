
package org.opencpn.opencpn;

import android.app.Activity;
import android.os.Bundle;
import org.opencpn.opencpn.R;
import android.content.Intent;
import android.util.Log;
import android.net.Uri ;
import java.io.File;
import java.io.IOException;
import java.io.OutputStream;
import java.io.InputStream;
import java.io.FileOutputStream;
import java.io.FileInputStream;

public class OCPNPluginInstallerActivity extends Activity {

        public void onCreate(Bundle savedInstanceState) {

            super.onCreate(savedInstanceState);

            Log.i("OpenCPN", "OCPNPluginInstallerActivity onCreate");

            // Get intent, action and MIME type
            Intent intent = getIntent();
            String action = intent.getAction();
            String type = intent.getType();

            if (Intent.ACTION_SEND.equals(action) && type != null) {
                if ("text/plain".equals(type)) {
                    Log.i("OpenCPN", "OCPNPluginInstallerActivity Got text/plain");
                    handleSendText(intent); // Handle uri being sent
                }
            }

            finish();
        }

        void handleSendText(Intent intent) {
            Uri uri = (Uri) getIntent().getExtras().get(Intent.EXTRA_STREAM);
            String sharedFile = uri.toString();

            if (sharedFile != null) {
                Log.i("OpenCPN", "OCPNPluginInstallerActivity Got uri: " + sharedFile);



                // Name and extension of the shared file
                String fileName = new File(uri.getPath()).getName();


                //  Open the file as an input stream
                InputStream inputStream = null;
                try{
                    inputStream = getContentResolver().openInputStream(uri);

                    String length = String.valueOf(inputStream.available());
                    Log.i("OpenCPN", "OCPNPluginInstallerActivity: inputStream available(): " + length);
                }
                catch (Exception e) {
                    e.printStackTrace();
                    Log.i("OpenCPN", "OCPNPluginInstallerActivity:  Input Stream Exception");
                }

                // Create the destination directory
                String stagingPath = getFilesDir().getPath() + File.separator + "staging";
                Log.i("OpenCPN", "OCPNPluginInstallerActivity:  Staging directory: " + stagingPath);

                File stagingDirFile = new File(stagingPath);
                if (!stagingDirFile.exists()){
                    Log.i("OpenCPN", "OCPNPluginInstallerActivity:  Making Staging directory: " + stagingPath);
                    if( !stagingDirFile.mkdirs()){
                        Log.i("OpenCPN", "OCPNPluginInstallerActivity:  Could not make Staging directory: " + stagingPath);
                    }
                }

                // Create the output stream

                Log.i("OpenCPN", "OCPNPluginInstallerActivity:  Destination file: " + stagingPath + File.separator + fileName);

                File destinationFile = new File(stagingPath + File.separator + fileName);

                OutputStream outputStream = null;
                try{
                    destinationFile.createNewFile();
                    outputStream = new FileOutputStream(destinationFile);
                }
                catch (Exception e) {
                    e.printStackTrace();
                    Log.i("OpenCPN", "OCPNPluginInstallerActivity:  Output Stream Exception");
                }

                if(null != outputStream){
                    //  Copy the file to staging directory
                    try {
                        copyFile(inputStream, outputStream);
                        inputStream.close();
                        outputStream.close();
                        Log.i("OpenCPN", "OCPNPluginInstallerActivity: copyFile OK");
                    }
                    catch (Exception e) {
                        e.printStackTrace();
                        Log.i("OpenCPN", "OCPNPluginInstallerActivity: copyFile Exception");
                    }
                }

            }

        }

        static private void copyFile(InputStream inputStream, OutputStream outputStream)
            throws IOException
        {
            byte[] buffer = new byte[1024];

            int count;
            while ((count = inputStream.read(buffer)) > 0)
                outputStream.write(buffer, 0, count);
        }

        @Override
        public void finish() {
            Log.i("OpenCPN", "OCPNPluginInstallerActivity finish " + this);

//            Bundle b = new Bundle();
//            b.putString("SettingsString", m_newSettings);
            Intent i = new Intent();
//            i.putExtras(b);
            setResult(RESULT_OK, i);

            super.finish();
        }


}
