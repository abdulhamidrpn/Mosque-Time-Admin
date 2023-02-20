package com.rpn.adminmosque.ui.fragment

import android.Manifest
import android.app.Activity
import android.content.Context
import android.content.DialogInterface
import android.content.Intent
import android.net.Uri
import android.os.Bundle
import android.provider.Settings
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.Toast
import androidx.appcompat.app.AlertDialog
import androidx.lifecycle.Observer
import androidx.test.core.app.ApplicationProvider
import com.bumptech.glide.Glide
import com.bumptech.glide.load.engine.DiskCacheStrategy
import com.karumi.dexter.Dexter
import com.karumi.dexter.MultiplePermissionsReport
import com.karumi.dexter.PermissionToken
import com.karumi.dexter.listener.PermissionRequest
import com.karumi.dexter.listener.multi.MultiplePermissionsListener
import com.rpn.adminmosque.R
import com.rpn.adminmosque.databinding.FragmentAddMessageBinding
import com.rpn.adminmosque.extensions.notifyObserver
import com.rpn.adminmosque.utils.GeneralUtils.loadingDialog
import com.rpn.adminmosque.utils.Status


class AddMessageFragment : CoroutineFragment() {

    private var _binding: FragmentAddMessageBinding? = null

    // This property is only valid between onCreateView and
    // onDestroyView.
    private val binding get() = _binding!!


    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        _binding = FragmentAddMessageBinding.inflate(inflater, container, false)
        val root: View = binding.root

        return root
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {

        binding.apply {
            ivMsg1.setOnClickListener {
                requestPermissions(1)
            }
            ivMsg2.setOnClickListener {
                requestPermissions(2)
            }
            ivMsg3.setOnClickListener {
                requestPermissions(3)
            }
            ivMsg4.setOnClickListener {
                requestPermissions(4)
            }
            ivMsg5.setOnClickListener {
                requestPermissions(5)
            }
            ivMsg6.setOnClickListener {
                requestPermissions(6)
            }
            ivMsg7.setOnClickListener {
                requestPermissions(7)
            }

            //upload button
            btnUpload1.setOnClickListener {
                requireContext().loadingDialog { dialog ->
                    mainViewModel.updateMessage(0) {
                        binding.tvMsg1.text = "1st Message Updated"
                        dialog.dismiss()
                        Toast.makeText(requireContext(), it, Toast.LENGTH_SHORT).show()
                    }
                }

            }
            btnUpload2.setOnClickListener {
                requireContext().loadingDialog { dialog ->
                    mainViewModel.updateMessage(1) {
                        binding.tvMsg2.text = "2nd Message Updated"
                        dialog.dismiss()
                        Toast.makeText(requireContext(), it, Toast.LENGTH_SHORT).show()
                    }
                }
            }
            btnUpload3.setOnClickListener {
                requireContext().loadingDialog { dialog ->
                    mainViewModel.updateMessage(2) {
                        binding.tvMsg3.text = "3rd Message Updated"
                        dialog.dismiss()
                        Toast.makeText(requireContext(), it, Toast.LENGTH_SHORT).show()
                    }
                }
            }
            btnUpload4.setOnClickListener {
                requireContext().loadingDialog { dialog ->
                    mainViewModel.updateMessage(3) {
                        binding.tvMsg4.text = "1st Message Updated"
                        dialog.dismiss()
                        Toast.makeText(requireContext(), it, Toast.LENGTH_SHORT).show()
                    }
                }
            }
            btnUpload5.setOnClickListener {
                requireContext().loadingDialog { dialog ->
                    mainViewModel.updateMessage(4) {
                        binding.tvMsg5.text = "2nd Message Updated"
                        dialog.dismiss()
                        Toast.makeText(requireContext(), it, Toast.LENGTH_SHORT).show()
                    }
                }
            }
            btnUpload6.setOnClickListener {
                requireContext().loadingDialog { dialog ->
                    mainViewModel.updateMessage(5) {
                        binding.tvMsg6.text = "3rd Message Updated"
                        dialog.dismiss()
                        Toast.makeText(requireContext(), it, Toast.LENGTH_SHORT).show()
                    }
                }
            }
            btnUpload7.setOnClickListener {
                requireContext().loadingDialog { dialog ->
                    mainViewModel.updateMessage(6) {
                        binding.tvMsg7.text = "Countdown Updated"
                        dialog.dismiss()
                        Toast.makeText(requireContext(), it, Toast.LENGTH_SHORT).show()
                    }
                }
            }
        }
        binding.btnUpdateMsg.setOnClickListener {

            showConfirmDialog() {
                if (it) {
                    if (mainViewModel.imgMsg.value != null) {

                        binding.btnUploadProgress.visibility = View.VISIBLE
                        binding.btnUpdateMsg.visibility = View.GONE

                        mainViewModel.updateMessage {

                            if (it.peekContent().status == Status.SUCCESS) {
                                Toast.makeText(
                                    requireContext(),
                                    it.peekContent().message,
                                    Toast.LENGTH_SHORT
                                ).show()
                                binding.apply {
                                    btnUploadProgress.visibility = View.GONE
                                    btnUpdateMsg.visibility = View.VISIBLE
                                }
                            } else {
                                Toast.makeText(
                                    requireContext(),
                                    it.peekContent().message,
                                    Toast.LENGTH_SHORT
                                ).show()
                                binding.apply {
                                    btnUploadProgress.visibility = View.GONE
                                    btnUpdateMsg.visibility = View.VISIBLE
                                }
                            }
                        }
                    }
                } else {
                    return@showConfirmDialog
                }
            }

            return@setOnClickListener

        }
        observeInfo()
    }

    fun loadImage(index: Int = 0, view: ImageView) {
        try {
            Glide
                .with(requireContext())
                .load(mainViewModel.imgMsg.value?.get(index))
                .placeholder(R.drawable.ic_round_add_photo)
                .error(R.drawable.ic_round_error)
                .diskCacheStrategy(DiskCacheStrategy.DATA)
                .into(view)
        } catch (e: Exception) {
            e.printStackTrace()
        }
    }

    fun observeInfo() {

        mainViewModel.imgMsg.observe(viewLifecycleOwner, Observer { list ->
            for (i in 0..list.size - 1) {
                when (i) {
                    0 -> {
                        loadImage(0, binding.ivMsg1)
                    }
                    1 -> {
                        loadImage(1, binding.ivMsg2)
                    }
                    2 -> {
                        loadImage(2, binding.ivMsg3)
                    }
                    3 -> {
                        loadImage(3, binding.ivMsg4)
                    }
                    4 -> {
                        loadImage(4, binding.ivMsg5)
                    }
                    5 -> {
                        loadImage(5, binding.ivMsg6)
                    }
                    6 -> {
                        loadImage(6, binding.ivMsg7)
                    }
                }
            }
        })
    }

    val TAG = "AddMessageTAG"
    fun setImage(index: Int, uri: String = selecteduri.toString()) {
        try {
            mainViewModel.imgMsg.value?.set(index, uri)
        } catch (e: Exception) {
            mainViewModel.imgMsg.value?.add(index, uri)
        }

        mainViewModel.imgMsg.notifyObserver()
    }

    var selecteduri: Uri? = null
    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        if (resultCode == Activity.RESULT_OK && data != null) {
            selecteduri = data.data
            when (requestCode) {

                1 -> {
                    setImage(0)
                }
                2 -> {
                    setImage(1)
                }
                3 -> {
                    setImage(2)
                }
                4 -> {
                    setImage(3)
                }
                5 -> {
                    setImage(4)
                }
                6 -> {
                    setImage(5)
                }
                7 -> {
                    //for countdown
                    setImage(6)
                }
            }


        }

    }

    private fun requestPermissions(requestCode: Int) {
        // below line is use to request
        // permission in the current activity.
        Dexter.withActivity(requireActivity()) // below line is use to request the number of
            // permissions which are required in our app.
            .withPermissions(
                Manifest.permission.READ_EXTERNAL_STORAGE,  // below is the list of permissions
                Manifest.permission.WRITE_EXTERNAL_STORAGE
            ) // after adding permissions we are
            // calling an with listener method.
            .withListener(object : MultiplePermissionsListener {
                override fun onPermissionsChecked(multiplePermissionsReport: MultiplePermissionsReport) {
                    // this method is called when all permissions are granted
                    if (multiplePermissionsReport.areAllPermissionsGranted()) {
                        val intent = Intent(Intent.ACTION_PICK)
                        intent.type = "image/*"
                        startActivityForResult(intent, requestCode)

                    }
                    // check for permanent denial of any permission
                    if (multiplePermissionsReport.isAnyPermissionPermanentlyDenied) {
                        // permission is denied permanently,
                        // we will show user a dialog message.
                        showSettingsDialog()
                    }
                }

                override fun onPermissionRationaleShouldBeShown(
                    list: List<PermissionRequest?>?,
                    permissionToken: PermissionToken
                ) {
                    // this method is called when user grants some
                    // permission and denies some of them.
                    permissionToken.continuePermissionRequest()
                }
            }).withErrorListener { // we are displaying a toast message for error message.
                Toast.makeText(
                    ApplicationProvider.getApplicationContext<Context>(),
                    "Error occurred! ",
                    Toast.LENGTH_SHORT
                ).show()
            } // below line is use to run the permissions
            // on same thread and to check the permissions
            .onSameThread().check()
    }

    // below is the shoe setting dialog
    // method which is use to display a
    // dialogue message.
    private fun showSettingsDialog() {
        // we are displaying an alert dialog for permissions
        val builder: AlertDialog.Builder = AlertDialog.Builder(requireContext())

        // below line is the title
        // for our alert dialog.
        builder.setTitle("Need Permissions")

        // below line is our message for our dialog
        builder.setMessage("This app needs permission to use this feature. You can grant them in app settings.")
        builder.setPositiveButton("GOTO SETTINGS",
            DialogInterface.OnClickListener { dialog, which -> // this method is called on click on positive
                // button and on clicking shit button we
                // are redirecting our user from our app to the
                // settings page of our app.
                dialog.cancel()
                // below is the intent from which we
                // are redirecting our user.
                val intent = Intent(Settings.ACTION_APPLICATION_DETAILS_SETTINGS)
                val uri = Uri.fromParts("package", requireContext().getPackageName(), null)
                intent.data = uri
                startActivityForResult(intent, 101)
            })
        builder.setNegativeButton("Cancel",
            DialogInterface.OnClickListener { dialog, which -> // this method is called when
                // user click on negative button.
                dialog.cancel()
            })
        // below line is used
        // to display our dialog
        builder.show()
    }


    fun showConfirmDialog(msg: String? = null, onConfirm: (confirmed: Boolean) -> Unit) {
        val builder = AlertDialog.Builder(requireContext())
        builder.setTitle("Do you want to update the message")
        builder.setMessage("")

        builder.setPositiveButton("Update") { dialog, p1 ->
            dialog.dismiss()
            onConfirm(true)
        }

        builder.setNegativeButton("CANCEL") { dialog, p1 ->
            dialog.cancel()
            onConfirm(false)
        }

        builder.show();
    }
}