package alex.orobinsk.vortex.ui.viewModel

import alex.orobinsk.vortex.ui.base.BaseViewModel
import android.app.Activity
import android.database.Cursor
import android.graphics.Bitmap
import android.net.Uri
import android.provider.MediaStore
import android.provider.MediaStore.MediaColumns
import android.util.Log
import androidx.lifecycle.MutableLiveData


class ShareViewModel: BaseViewModel() {
    val bitmap = MutableLiveData<Bitmap>()
    override fun onCreated() {}

    private fun getAllShownImagesPath(activity: Activity): ArrayList<String> {
        val uri: Uri = android.provider.MediaStore.Images.Media.EXTERNAL_CONTENT_URI
        val cursor: Cursor?
        val columnIndexData: Int?
        val column_index_folder_name: Int?
        val listOfAllImages = ArrayList<String>()
        var absolutePathOfImage: String? = null

        val projection = arrayOf(MediaColumns.DATA, MediaStore.Images.Media.BUCKET_DISPLAY_NAME)

        cursor = activity.contentResolver.query(uri, projection, null, null, null)

        columnIndexData = cursor?.getColumnIndexOrThrow(MediaColumns.DATA)
        column_index_folder_name = cursor?.getColumnIndexOrThrow(MediaStore.Images.Media.BUCKET_DISPLAY_NAME)
        cursor?.let { _cursor ->
            while (_cursor.moveToNext()) {
                columnIndexData?.let {  _columnIndexData ->
                    _cursor.getString(_columnIndexData).let {

                    }
                }

            }
        }

        return listOfAllImages
    }
}