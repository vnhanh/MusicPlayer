package com.vnhanh.musicplayer.util

import android.content.Context
import android.os.Build
import android.os.Environment
import android.os.Environment.isExternalStorageRemovable
import com.vnhanh.musicplayer.BuildConfig
import java.io.File

object FileUtil {
    private const val ANDROID_EXT_DATA_PREFIX = "/Android/data/"
    private const val FAVORITE_IMAGE = "FavoriteImage"
    private var imageCounter = 0

    fun getFavoriteImageFile(context: Context?, inquireFileName:String?) : File?{
        if(context == null) return null

        val fileName: String = if (inquireFileName.isNullOrEmpty()) getAutoImageFileName() else inquireFileName
        val savedFilename: String = if (fileName.indexOf(".") > -1) fileName else "$fileName.png"

        val dirPathToSave: File? = context.getExternalFilesDir(FAVORITE_IMAGE)
        if (dirPathToSave != null && !dirPathToSave.exists()) {
            dirPathToSave.mkdirs()
        }

        val dir: File? = if (isSDCARDMounted()) {
            val dirPathToSave: File? = when(BuildConfig.VERSION_CODE){
                Build.VERSION_CODES.Q -> {
                    context.getExternalFilesDir("FavoriteImage")
                }

                else -> File(Environment.getExternalStorageDirectory().toString()
                        + ANDROID_EXT_DATA_PREFIX + context.packageName + "/$FAVORITE_IMAGE/")
            }

            if (dirPathToSave != null && !dirPathToSave.exists()) {
                dirPathToSave.mkdirs()
            }
            dirPathToSave
        } else {
            context.filesDir
        }

        return File(dir, savedFilename)
    }

    private fun getAutoImageFileName() : String{
        val suffix:String = when(imageCounter){
            in 0..9 -> String.format("00%d", imageCounter)
            in 10..99 -> String.format("0%d", imageCounter)
            else -> imageCounter.toString()
        }

        return String.format("music_image_%s", suffix)
    }

    private fun isSDCARDMounted(): Boolean {
        return Environment.MEDIA_MOUNTED == Environment.getExternalStorageState() || !isExternalStorageRemovable()
    }
}