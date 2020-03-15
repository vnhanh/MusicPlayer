package com.vnhanh.musicplayer.util

//import io.reactivex.Completable
import android.content.ContentValues
import android.content.Context
import android.content.res.Resources
import android.graphics.*
import android.graphics.drawable.BitmapDrawable
import android.graphics.drawable.Drawable
import android.graphics.drawable.VectorDrawable
import android.media.MediaScannerConnection
import android.net.Uri
import android.os.Build
import android.os.Environment
import android.provider.MediaStore
import android.util.Base64
import android.view.View
import androidx.appcompat.content.res.AppCompatResources
import androidx.core.content.FileProvider
import androidx.core.graphics.drawable.DrawableCompat
import androidx.core.graphics.drawable.RoundedBitmapDrawableFactory
import androidx.vectordrawable.graphics.drawable.VectorDrawableCompat
import com.bumptech.glide.request.RequestOptions
import com.google.android.gms.common.util.IOUtils
import com.google.firebase.crashlytics.FirebaseCrashlytics
import com.vnhanh.musicplayer.BuildConfig
import java.io.*

object ImageUtil {
    private const val DIRECTORY_NAME = "/MUSIC"
    private var roundedTopRequestOptions: RequestOptions? = null
    fun encodeToBase64(image: Bitmap): String? {
        var image = image
        var result: String? = null
        val byteArrayOS = ByteArrayOutputStream()
        try {
            image = resizeBitmap(image)
            image.compress(Bitmap.CompressFormat.JPEG, 80, byteArrayOS)
            result = Base64.encodeToString(
                byteArrayOS.toByteArray(),
                Base64.NO_WRAP
            )
        } catch (e: Exception) {
        } finally {
            IOUtils.closeQuietly(byteArrayOS)
        }
        return result
    }

    fun resizeBitmap(image: Bitmap): Bitmap {
        var image: Bitmap = image
        if (image.width > 300) {
            image = Bitmap.createScaledBitmap(image, 300, 300, false)
        }
        return image
    }

    fun decodeBase64(input: String?): Bitmap {
        val decodedBytes = Base64.decode(input, 0)
        return BitmapFactory.decodeByteArray(decodedBytes, 0, decodedBytes.size)
    }

    fun saveFavoriteImageToCache(
        context: Context?,
        templateId: String?,
        imageBase64: String?
    ) {
        val favoriteFile: File? = FileUtil.getFavoriteImageFile(context, templateId)
        if (favoriteFile == null) return

        try {
            FileOutputStream(favoriteFile).use { fOut ->
                val bitmap = decodeBase64(imageBase64)
                bitmap.compress(Bitmap.CompressFormat.PNG, 85, fOut)
                fOut.flush()
                bitmap.recycle()
            }
        } catch (e: Exception) {
            e.printStackTrace()
        }
    }

    fun deleteFavoriteImageFromCache(
        context: Context?,
        templateId: String?
    ) {
        val favoriteFile: File? = FileUtil.getFavoriteImageFile(context, templateId)

        if (favoriteFile == null) return

        if (favoriteFile.exists()) {
            favoriteFile.delete()
        }
    }

    fun saveBitmapToFolder(
        b: Bitmap,
        fileName: String,
        context: Context
    ): Boolean {
        var fileOutputStream: FileOutputStream? = null
        try {
            val path =
                Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_PICTURES)
            if (!path.exists()) {
                path.mkdir()
            }
            val kplusPath =
                File(path, DIRECTORY_NAME)
            if (!kplusPath.exists()) {
                kplusPath.mkdir()
            }
            val savePath = File(kplusPath, "/$fileName.jpeg")

//            Bitmap b = getBitmapFromView(bitmap);
            fileOutputStream = FileOutputStream(savePath)
            b.compress(Bitmap.CompressFormat.JPEG, 60, fileOutputStream)
            fileOutputStream.flush()
            b.recycle()

            // Refresh file
            MediaScannerConnection.scanFile(
                context,
                arrayOf(savePath.path),
                arrayOf("image/jpeg"),
                null
            )
            addImageGallery(context, savePath)
        } catch (e: Exception) {
            if (BuildConfig.USE_CRASHLYTICS) {

                FirebaseCrashlytics.getInstance().setCustomKey("source", "try_catch")
                FirebaseCrashlytics.getInstance().setCustomKey("class", "ImageUtil")
                FirebaseCrashlytics.getInstance()
                    .setCustomKey("method", "saveBitmapToKPlusFolder(Bitmap, String, Context)")
                FirebaseCrashlytics.getInstance()
                    .log(e.message ?: "exception on saveBitmapToFolder()")
            }
            return false
        } finally {
            IOUtils.closeQuietly(fileOutputStream)
        }
        return true
    }

    private fun addImageGallery(context: Context, file: File) {
        // notify the gallery DB that another file was inserted.
        val values = ContentValues()
        values.put(MediaStore.Images.Media.DATA, file.absolutePath)
        values.put(MediaStore.Images.Media.MIME_TYPE, "image/jpeg") // or image/png
        context.contentResolver.insert(MediaStore.Images.Media.EXTERNAL_CONTENT_URI, values)
    }

//    fun saveBitmapToKPlusFolderAsyncTask(
//        b: Bitmap,
//        fileName: String,
//        context: Context
//    ): Completable {
//        return Completable.create({ emitter ->
//            var fileOutputStream: FileOutputStream? = null
//            try {
//                val path =
//                    Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_PICTURES)
//                if (!path.exists()) {
//                    path.mkdir()
//                }
//                val kplusPath =
//                    File(path, DIRECTORY_NAME)
//                if (!kplusPath.exists()) {
//                    kplusPath.mkdir()
//                }
//                var savePath = File(kplusPath, "/$fileName.jpeg")
//                var count = 0
//                while (savePath.exists()) {
//                    count++
//                    savePath = File(kplusPath, "/$fileName($count).jpeg")
//                }
//                //                Bitmap b = getBitmapFromView(view);
//                fileOutputStream = FileOutputStream(savePath)
//                b.compress(Bitmap.CompressFormat.JPEG, 60, fileOutputStream)
//                fileOutputStream.flush()
//                // Refresh file
//                MediaScannerConnection.scanFile(
//                    context,
//                    arrayOf(savePath.path),
//                    arrayOf("image/jpeg"),
//                    null
//                )
//                addImageGallery(context, savePath)
//                emitter.onComplete()
//            } catch (e: Exception) {
//                if (BuildConfig.USE_CRASHLYTICS) {
//                    FirebaseCrashlytics.getInstance().setCustomKey("source", "try_catch")
//                    FirebaseCrashlytics.getInstance().setCustomKey("class", "ImageUtil")
//                    FirebaseCrashlytics.getInstance().setCustomKey(
//                        "method",
//                        "saveBitmapToKPlusFolderAsyncTask(Bitmap, String, Context)"
//                    )
//                    Crashlytics.logException(e)
//                }
//                emitter.tryOnError(e.cause)
//            } finally {
//                IOUtils.closeQuietly(fileOutputStream)
//            }
//        })
//    }

    fun getImageFile(fileName: String): File? {
        return try {
            val path =
                Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_PICTURES)
            if (!path.exists()) {
                path.mkdir()
            }
            val kplusPath =
                File(path, DIRECTORY_NAME)
            File(kplusPath, "/$fileName.jpeg")
        } catch (e: Exception) {
            null
        }
    }

    fun getShareImageUri(
        context: Context,
        view: View,
        fileName: String
    ): Uri? {
        val path =
            Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_PICTURES)
        if (!path.exists()) {
            path.mkdir()
        }
        val kplusPath =
            File(path, DIRECTORY_NAME)
        if (!kplusPath.exists()) {
            kplusPath.mkdir()
        }
        val savePath = File(kplusPath, "/$fileName.jpeg")
        if (!savePath.exists()) {
            val b = getBitmapFromView(view)
            var fileOutputStream: FileOutputStream? = null
            try {
                fileOutputStream = FileOutputStream(savePath)
                b.compress(Bitmap.CompressFormat.JPEG, 60, fileOutputStream)
                fileOutputStream.flush()
                b.recycle()
            } catch (e: FileNotFoundException) {
                return null
            } catch (e: IOException) {
                return null
            } finally {
                IOUtils.closeQuietly(fileOutputStream)
                val contentUri: Uri
                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
                    contentUri = FileProvider.getUriForFile(
                        context,
                        context.packageName + ".fileprovider",
                        savePath
                    )
                } else {
                    contentUri = Uri.fromFile(savePath)
                    //                contentUri = Uri.fromFile(getImageFile(fileName));
                }
                return contentUri
            }
        } else {
            val contentUri: Uri
            contentUri = if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
                FileProvider.getUriForFile(
                    context,
                    context.packageName + ".fileprovider",
                    savePath
                )
            } else {
                Uri.fromFile(savePath)
                //                contentUri = Uri.fromFile(getImageFile(fileName));
            }
            return contentUri
        }
    }

    @Throws(IOException::class)
    fun copyToTemp(src: File?, dst: File): File {
        val `in`: InputStream = FileInputStream(src)
        val out: OutputStream = FileOutputStream(dst)
        try {
            // Transfer bytes from in to out
            val buf = ByteArray(1024)
            var len: Int
            while (`in`.read(buf).also { len = it } > 0) {
                out.write(buf, 0, len)
            }
        } finally {
            IOUtils.closeQuietly(`in`)
            IOUtils.closeQuietly(out)
        }
        return dst
    }

    fun getCroppedBitmap(bitmap: Bitmap): Bitmap {
        val output = Bitmap.createBitmap(
            bitmap.width,
            bitmap.height, Bitmap.Config.ARGB_8888
        )
        val canvas = Canvas(output)
        val color = -0xbdbdbe
        val paint = Paint()
        val rect =
            Rect(0, 0, bitmap.width, bitmap.height)
        paint.isAntiAlias = true
        canvas.drawARGB(0, 0, 0, 0)
        paint.color = color
        // canvas.drawRoundRect(rectF, roundPx, roundPx, paint);
        canvas.drawCircle(
            bitmap.width / 2.toFloat(), bitmap.height / 2.toFloat(),
            bitmap.width / 2.toFloat(), paint
        )
        paint.xfermode = PorterDuffXfermode(PorterDuff.Mode.SRC_IN)
        canvas.drawBitmap(bitmap, rect, rect, paint)
        //Bitmap _bmp = Bitmap.createScaledBitmap(output, 60, 60, false);
        //return _bmp;
        return output
    }

    fun getBitmapFromVectorDrawable(context: Context?, drawableId: Int): Bitmap {
        var drawable =
            AppCompatResources.getDrawable(context!!, drawableId)
        if (Build.VERSION.SDK_INT < Build.VERSION_CODES.LOLLIPOP) {
            drawable = DrawableCompat.wrap(drawable!!).mutate()
        }
        return vectorDrawableToByteArray(drawable)
    }

    fun byteArrayToDrawable(
        res: Resources?,
        byteArrayImage: ByteArray
    ): Drawable? {
        return try {
            val bmp = BitmapFactory.decodeByteArray(byteArrayImage, 0, byteArrayImage.size)
            //            imageDrawable.setCircular(true);
//            imageDrawable.setCornerRadius(Math.max(bmp.getWidth(), bmp.getHeight()));
            RoundedBitmapDrawableFactory.create(res!!, bmp)
        } catch (e: Exception) {
//            if (BuildConfig.USE_CRASHLYTICS) {
//                FirebaseCrashlytics.getInstance().setCustomKey("source", "try_catch")
//                FirebaseCrashlytics.getInstance().setCustomKey("class", "ImageUtil")
//                FirebaseCrashlytics.getInstance().setCustomKey("method", "byteArrayToDrawable(Resources, byte[])")
//                Crashlytics.logException(e)
//            }
            null
        }
    }

    fun drawableToByteArray(merchantLogo: Drawable): ByteArray? {
        val stream = ByteArrayOutputStream()
        val bitmap: Bitmap
        return try {
            bitmap = if (merchantLogo is VectorDrawable || merchantLogo is VectorDrawableCompat) {
                vectorDrawableToByteArray(merchantLogo)
            } else {
                (merchantLogo as BitmapDrawable).bitmap
            }
            bitmap.compress(Bitmap.CompressFormat.PNG, 100, stream)
            stream.toByteArray()
        } catch (e: Exception) {
//            if (BuildConfig.USE_CRASHLYTICS) {
//                FirebaseCrashlytics.getInstance().setCustomKey("source", "try_catch")
//                FirebaseCrashlytics.getInstance().setCustomKey("class", "ImageUtil")
//                FirebaseCrashlytics.getInstance().setCustomKey("method", "drawableToByteArray(Drawable)")
//                Crashlytics.logException(e)
//            }
            null
        } finally {
            IOUtils.closeQuietly(stream)
        }
    }

    private fun vectorDrawableToByteArray(merchantLogo: Drawable?): Bitmap {
        val bitmap = Bitmap.createBitmap(
            merchantLogo!!.intrinsicWidth,
            merchantLogo.intrinsicHeight, Bitmap.Config.ARGB_8888
        )
        val canvas = Canvas(bitmap)
        merchantLogo.setBounds(0, 0, canvas.width, canvas.height)
        merchantLogo.draw(canvas)
        return bitmap
    }

    fun getBitmapFromView(view: View): Bitmap {
        val b = Bitmap.createBitmap(
            view.measuredWidth,
            view.measuredHeight,
            Bitmap.Config.ARGB_8888
        )
        val c = Canvas(b)
        view.layout(view.left, view.top, view.right, view.bottom)
        view.draw(c)
        return b
    }

//    fun getRoundedTopRequestOptions(context: Context): RequestOptions? {
//        if (roundedTopRequestOptions == null) {
//            roundedTopRequestOptions =
//                RequestOptions().transforms(
//                    CenterCrop(), RoundedCornersTransformation(
//                        context,
//                        context.resources.getDimensionPixelSize(
//                            R.dimen.home_card_horizontalscroll_layout_favorite_item_card_corner_radius
//                        ),
//                        0, RoundedCornersTransformation.CornerType.TOP
//                    )
//                )
//        }
//        return roundedTopRequestOptions
//    }

//    fun saveImageInfoInternalStorage(
//        context: Context,
//        bitmap: Bitmap,
//        imageName: String,
//        folderName: String?
//    ): Completable {
//        return Completable.create({ callback ->
//            try {
//                val folder = File(context.cacheDir, folderName)
//                if (!folder.exists()) {
//                    folder.mkdirs()
//                }
//                val stream =
//                    FileOutputStream("$folder/$imageName.jpeg") // overwrites this image every time
//                bitmap.compress(Bitmap.CompressFormat.JPEG, 100, stream)
//                stream.close()
//                callback.onComplete()
//            } catch (e: Exception) {
//                callback.onError(e.cause)
//            }
//        })
//    }

    fun getInternalImagePath(
        context: Context,
        imageName: String,
        folderName: String?
    ): Uri {
        val imagePath = File(context.cacheDir, folderName)
        val savePath = File(imagePath, "$imageName.jpeg")
        return FileProvider.getUriForFile(
            context,
            context.packageName + ".fileprovider",
            savePath
        )
    }
}