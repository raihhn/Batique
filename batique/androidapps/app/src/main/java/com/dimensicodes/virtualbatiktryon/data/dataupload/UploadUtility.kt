package com.dimensicodes.virtualbatiktryon.data.dataupload


import android.app.Activity
import android.app.ProgressDialog
import android.net.Uri
import android.util.Log
import android.webkit.MimeTypeMap
import android.widget.Toast
import okhttp3.*
import okhttp3.MediaType.Companion.toMediaTypeOrNull
import okhttp3.RequestBody.Companion.asRequestBody
import java.io.File
import java.util.concurrent.TimeUnit

class UploadUtility(activity: Activity) {

    var activity = activity;
    var dialog: ProgressDialog? = null
    val client = OkHttpClient.Builder()
            .connectTimeout(180, TimeUnit.SECONDS)
            .writeTimeout(180, TimeUnit.SECONDS)
            .readTimeout(150, TimeUnit.SECONDS)
            .build()

    fun uploadFile(sourceFilePath: String, uploadedFileName: String? = null,func: (m: String) -> Unit) {
        uploadFile(File(sourceFilePath), uploadedFileName,func)
    }

    fun uploadFile(sourceFileUri: Uri, uploadedFileName: String? = null,func: (m: String) -> Unit) {
        val pathFromUri = URIPathHelper().getPath(activity,sourceFileUri)
        uploadFile(File(pathFromUri), uploadedFileName,func)
    }

    fun uploadFile(sourceFile: File, uploadedFileName: String? = null,func: (m: String) -> Unit) {
        Thread {
            val mimeType = getMimeType(sourceFile) ?: return@Thread
            var fileName: String = uploadedFileName ?: sourceFile.name
            fileName = System.currentTimeMillis().toString()+fileName
            toggleProgressDialog(true,"upload file")
            try {
                val requestBody: RequestBody =
                    MultipartBody.Builder().setType(MultipartBody.FORM)
                        .addFormDataPart("file", fileName,sourceFile.asRequestBody(mimeType.toMediaTypeOrNull()))
                        .build()
                val request: Request = Request.Builder().url(domainConnect.serverURL).post(requestBody).build()
                val response: Response = client.newCall(request).execute()
                if (response.isSuccessful) {
                    Log.d("ricky","success, path: "+response.request.url.toString())
                    func(response.request.url.toString())
                } else {
                    Log.e("File upload", "failed")
                    showToast("File uploading failed")
                }
            } catch (ex: Exception) {
                ex.printStackTrace()
                Log.e("File upload", "failed ${ex.message}")
                showToast("File uploading failed")
            }
            toggleProgressDialog(false)
        }.start()
    }
    fun getUrlfromWeb(filename:String,func: (m: String) -> Unit){
        Thread {
            toggleProgressDialog(true, "get $filename")
            try {
                val request: Request = Request.Builder().url(domainConnect.serverURL+filename).get().build()
                val response = client.newCall(request).execute()
                if (response.isSuccessful) {
                    func(response.request.url.toString())
                } else {
                    showToast("request failed failed")
                }
            } catch (ex: Exception) {
                ex.printStackTrace()
                Log.e("ricky", "failed")
            }
            toggleProgressDialog(false)
        }.start()
    }

    // url = file path or whatever suitable URL you want.
    fun getMimeType(file: File): String? {
        var type: String? = null
        val extension = MimeTypeMap.getFileExtensionFromUrl(file.path)
        if (extension != null) {
            type = MimeTypeMap.getSingleton().getMimeTypeFromExtension(extension)
        }
        return type
    }

    fun showToast(message: String) {
        activity.runOnUiThread {
            Toast.makeText( activity, message, Toast.LENGTH_LONG ).show()
        }
    }

    fun toggleProgressDialog(show: Boolean,string: String? = null) {
        activity.runOnUiThread {
            if (show) {
                dialog = ProgressDialog.show(activity, "", string!!, true);
            } else {
                dialog?.dismiss();
            }
        }
    }


}