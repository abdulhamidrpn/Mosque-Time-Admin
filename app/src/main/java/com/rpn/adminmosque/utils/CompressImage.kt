package com.rpn.adminmosque.utils

import android.content.Context
import android.database.Cursor
import android.graphics.*
import android.media.ExifInterface
import android.net.Uri
import android.os.Environment
import android.provider.MediaStore
import android.util.Log
import java.io.File
import java.io.FileNotFoundException
import java.io.FileOutputStream
import java.io.IOException


fun compressImage(context: Context, uri: Uri?): String? {
    Log.d("compressTAG", "uri is $uri")
    val filePath = getRealPathFromURI(context, uri)
    var scaledBitmap: Bitmap? = null
    val options = BitmapFactory.Options()
    Log.d("compressTAG", "filePath $filePath")

//      by setting this field as true, the actual bitmap pixels are not loaded in the memory. Just the bounds are loaded. If
//      you try the use the bitmap here, you will get null.
    options.inJustDecodeBounds = true
    var bmp = BitmapFactory.decodeFile(filePath, options)
    var actualHeight = options.outHeight
    var actualWidth = options.outWidth

//      max Height and width values of the compressed image is taken as 816x612
    val maxHeight = 816.0f
    val maxWidth = 612.0f
    var imgRatio = 0f
    try {
        Log.d(
            "compressTAG",
            "imageratio $imgRatio actualHeight $actualHeight actualWidth $actualWidth"
        )
        imgRatio = (actualWidth / actualHeight).toFloat()

    } catch (e: Exception) {
        Log.d("compressTAG", "exception ${e.message}")

    }
    val maxRatio = maxWidth / maxHeight

//      width and height values are set maintaining the aspect ratio of the image
    if (actualHeight > maxHeight || actualWidth > maxWidth) {
        if (imgRatio < maxRatio) {
            imgRatio = maxHeight / actualHeight
            actualWidth = (imgRatio * actualWidth).toInt()
            actualHeight = maxHeight.toInt()
        } else if (imgRatio > maxRatio) {
            imgRatio = maxWidth / actualWidth
            actualHeight = (imgRatio * actualHeight).toInt()
            actualWidth = maxWidth.toInt()
        } else {
            actualHeight = maxHeight.toInt()
            actualWidth = maxWidth.toInt()
        }
    }

//      setting inSampleSize value allows to load a scaled down version of the original image
    options.inSampleSize = calculateInSampleSize(options, actualWidth, actualHeight)

//      inJustDecodeBounds set to false to load the actual bitmap
    options.inJustDecodeBounds = false

//      this options allow android to claim the bitmap memory if it runs low on memory
    options.inPurgeable = true
    options.inInputShareable = true
    options.inTempStorage = ByteArray(16 * 1024)
    try {
//          load the bitmap from its path
        bmp = BitmapFactory.decodeFile(filePath, options)
    } catch (exception: OutOfMemoryError) {
        exception.printStackTrace()
    }
    try {
        scaledBitmap = Bitmap.createBitmap(actualWidth, actualHeight, Bitmap.Config.ARGB_8888)
    } catch (exception: OutOfMemoryError) {
        exception.printStackTrace()
    }
    val ratioX = actualWidth / options.outWidth.toFloat()
    val ratioY = actualHeight / options.outHeight.toFloat()
    val middleX = actualWidth / 2.0f
    val middleY = actualHeight / 2.0f
    val scaleMatrix = Matrix()
    scaleMatrix.setScale(ratioX, ratioY, middleX, middleY)
    val canvas = Canvas(scaledBitmap!!)
    canvas.setMatrix(scaleMatrix)
    canvas.drawBitmap(
        bmp,
        middleX - bmp.width / 2,
        middleY - bmp.height / 2,
        Paint(Paint.FILTER_BITMAP_FLAG)
    )

//      check the rotation of the image and display it properly
    val exif: ExifInterface
    try {
        exif = ExifInterface(filePath)
        val orientation = exif.getAttributeInt(
            ExifInterface.TAG_ORIENTATION, 0
        )
        Log.d("EXIF", "Exif: $orientation")
        val matrix = Matrix()
        if (orientation == 6) {
            matrix.postRotate(90f)
            Log.d("EXIF", "Exif: $orientation")
        } else if (orientation == 3) {
            matrix.postRotate(180f)
            Log.d("EXIF", "Exif: $orientation")
        } else if (orientation == 8) {
            matrix.postRotate(270f)
            Log.d("EXIF", "Exif: $orientation")
        }
        scaledBitmap = Bitmap.createBitmap(
            scaledBitmap, 0, 0,
            scaledBitmap.width, scaledBitmap.height, matrix,
            true
        )
    } catch (e: IOException) {
        Log.d("compressTAG", "exception rtation ${e.message}")
    }
    var out: FileOutputStream? = null
    val filename: String = getFilename(filePath)
    try {
        out = FileOutputStream(filename)

//          write the compressed bitmap at the destination specified by filename.
        scaledBitmap!!.compress(Bitmap.CompressFormat.PNG, 100, out)
    } catch (e: FileNotFoundException) {
        Log.d("compressTAG", "exception writing bitmap ${e.message}")
    }
    Log.d("compressTAG", "fileName returning $filename")
    return filename
}

fun getFilename(filePath: String): String {

    val path = Environment.getExternalStoragePublicDirectory(
        Environment.DIRECTORY_PICTURES
    )
    val fileFrompath = File(filePath)
    val file = File(path, "ErfahCashImage/Images")
//    val file = File(Environment.getExternalStorageDirectory().path, "MyFolder/Images")
    if (!file.exists()) {
        file.mkdirs()
    }
//    val fileName = file.getAbsolutePath().toString() + "/" + System.currentTimeMillis() + ".jpg"
    val fileName = file.getAbsolutePath()
        .toString() + "/" + fileFrompath.nameWithoutExtension + "_compressed" + ".jpg"

    Log.d("compressTAG", "fileName $fileName")

    return fileName


}

private fun getRealPathFromURI(context: Context, contentURI: Uri?): String {
    val result: String
    val cursor: Cursor? = context.getContentResolver().query(contentURI!!, null, null, null, null)
    if (cursor == null) {
        result = contentURI.path!!
    } else {
        cursor.moveToFirst()
        val idx = cursor.getColumnIndex(MediaStore.Images.ImageColumns.DATA)
        result = cursor.getString(idx)
        cursor.close()
    }
    return result
}

fun calculateInSampleSize(options: BitmapFactory.Options, reqWidth: Int, reqHeight: Int): Int {
    val height = options.outHeight
    val width = options.outWidth
    var inSampleSize = 1
    if (height > reqHeight || width > reqWidth) {
        val heightRatio = Math.round(height.toFloat() / reqHeight.toFloat())
        val widthRatio = Math.round(width.toFloat() / reqWidth.toFloat())
        inSampleSize = if (heightRatio < widthRatio) heightRatio else widthRatio
    }
    val totalPixels = (width * height).toFloat()
    val totalReqPixelsCap = (reqWidth * reqHeight * 2).toFloat()
    while (totalPixels / (inSampleSize * inSampleSize) > totalReqPixelsCap) {
        inSampleSize++
    }
    Log.d("compressTAG", "size in calculate $inSampleSize")
    return inSampleSize
}