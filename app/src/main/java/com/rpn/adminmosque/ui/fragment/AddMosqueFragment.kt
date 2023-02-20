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
import android.widget.Toast
import androidx.appcompat.app.AlertDialog
import androidx.lifecycle.Observer
import androidx.test.core.app.ApplicationProvider
import com.bumptech.glide.Glide
import com.karumi.dexter.Dexter
import com.karumi.dexter.MultiplePermissionsReport
import com.karumi.dexter.PermissionToken
import com.karumi.dexter.listener.PermissionRequest
import com.karumi.dexter.listener.multi.MultiplePermissionsListener
import com.rpn.adminmosque.R
import com.rpn.adminmosque.databinding.FragmentAddMosqueBinding
import com.rpn.adminmosque.model.MasjidInfo
import com.rpn.adminmosque.utils.GeneralUtils
import com.rpn.adminmosque.utils.Status


class AddMosqueFragment : CoroutineFragment() {

    private var _binding: FragmentAddMosqueBinding? = null

    // This property is only valid between onCreateView and
    // onDestroyView.
    private val binding get() = _binding!!


    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        _binding = FragmentAddMosqueBinding.inflate(inflater, container, false)
        val root: View = binding.root

        return root
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {

        binding.imageView.setOnClickListener {
            requestPermissions()
        }
        mainViewModel.imgMosque.observe(viewLifecycleOwner, Observer {
            Glide.with(requireContext()).load(it)
                .placeholder(R.drawable.ic_round_add_photo)
                .error(R.drawable.ic_round_error)
                .into(binding.imageView)

        })


        binding.etJumma.setOnClickListener {
            GeneralUtils.setTimer(
                requireContext(),
                is24HourView = true
            ) {
                binding.etJumma.setText(GeneralUtils.sdfTime24.format(it))
            }
        }
        binding.btnUploadMosqueInfo.setOnClickListener {


            mainViewModel.masjidInfo.value = MasjidInfo()
            mainViewModel.masjidInfo.value?.apply {
                city = binding.etCity.text.trim().toString()
                bottomMessage = binding.etBottomMessage.text.trim().toString()
                country = binding.etCountry.text.trim().toString()
                jumua = binding.etJumma.text.trim().toString()
                latitude = binding.etLatitude.text.trim().toString()
                longitude = binding.etLongitude.text.trim().toString()
                masjidName = binding.etMosqueName.text.trim().toString()
                ownerName = settingsUtility.userName //binding.etOwnerName.text.trim().toString()
                email = settingsUtility.userEmail
                ownerUid = settingsUtility.userId
                phoneNumber = settingsUtility.userPhone
                documentId = settingsUtility.mosqueDocumentId
            }
            showConfirmDialog(mainViewModel.masjidInfo.value) {
                if (it) {
                    if (mainViewModel.masjidInfo.value != null &&
                        mainViewModel.masjidInfo.value!!.masjidName.isNotEmpty() &&
                        mainViewModel.masjidInfo.value!!.email.isNotEmpty() &&
                        mainViewModel.masjidInfo.value!!.country.isNotEmpty() &&
                        mainViewModel.masjidInfo.value!!.city.isNotEmpty() &&
                        mainViewModel.masjidInfo.value!!.jumua.isNotEmpty() &&
                        mainViewModel.masjidInfo.value!!.latitude.isNotEmpty() &&
                        mainViewModel.masjidInfo.value!!.longitude.isNotEmpty()
                    ) {

                        binding.btnUploadProgress.visibility = View.VISIBLE
                        binding.btnUploadMosqueInfo.visibility = View.GONE

                        if (binding.btnUploadMosqueInfo.text == getString(R.string.register_new_mosque)) {
                            mainViewModel.masjidInfo.value?.apply {
                                activated = false
                            }
                            mainViewModel.uploadMasjidInfo(
                                mainViewModel.masjidInfo.value!!,
                            ) {

                                if (it.peekContent().status == Status.SUCCESS) {
                                    binding.apply {
                                        btnUploadProgress.visibility = View.GONE
                                        btnUploadMosqueInfo.visibility = View.VISIBLE
                                        btnUploadMosqueInfo.text = "Update"

                                    }
                                    Toast.makeText(
                                        requireContext(),
                                        "${it.peekContent().message}",
                                        Toast.LENGTH_LONG
                                    ).show()
                                } else {

                                    binding.apply {
                                        btnUploadProgress.visibility = View.GONE
                                        btnUploadMosqueInfo.visibility = View.VISIBLE
                                    }

                                    Toast.makeText(
                                        requireContext(),
                                        it.peekContent().message,
                                        Toast.LENGTH_LONG
                                    )
                                        .show()
                                }

                            }
                        } else if (binding.btnUploadMosqueInfo.text == getString(R.string.update_information)) {

                            mainViewModel.masjidInfo.value?.apply {
                                image = settingsUtility.mosqueImage
                            }
                            mainViewModel.updateMasjidInfo(
                                mainViewModel.masjidInfo.value!!
                            ) {

                                if (it.peekContent().status == Status.SUCCESS) {
                                    binding.apply {
                                        btnUploadProgress.visibility = View.GONE
                                        btnUploadMosqueInfo.visibility = View.VISIBLE
                                        btnUploadMosqueInfo.text = "Update"

                                    }
                                    Toast.makeText(
                                        requireContext(),
                                        "${it.peekContent().message}",
                                        Toast.LENGTH_LONG
                                    ).show()
                                } else {

                                    binding.apply {
                                        btnUploadProgress.visibility = View.GONE
                                        btnUploadMosqueInfo.visibility = View.VISIBLE
                                    }

                                    Toast.makeText(
                                        requireContext(),
                                        it.peekContent().message,
                                        Toast.LENGTH_LONG
                                    )
                                        .show()
                                }

                            }
                        }

                    } else {
                        Toast.makeText(
                            requireContext(),
                            "Fill all info",
                            Toast.LENGTH_LONG
                        )
                            .show()
                    }
                } else {
                    return@showConfirmDialog
                }
            }

            return@setOnClickListener

        }

        observeInfo()


        mainViewModel.isMasjidActivated.observe(viewLifecycleOwner, Observer {
            checkMosqueActivated()
        })
    }

    fun checkMosqueActivated() {

        if (settingsUtility.mosqueDocumentId.isNotEmpty() && settingsUtility.mosqueDocumentId != "") {
            //Requested for mosque registration
            if (settingsUtility.mosqueActivated) {
                binding.btnUploadMosqueInfo.text = getString(R.string.update_information)
                binding.btnUploadMosqueInfo.isEnabled = true
            } else {
                binding.btnUploadMosqueInfo.text = "Pending"
                binding.btnUploadMosqueInfo.isEnabled = false
            }
        } else {
            //Register New Mosque
            binding.btnUploadMosqueInfo.text = getString(R.string.register_new_mosque)
            binding.btnUploadMosqueInfo.isEnabled = true
        }
    }

    fun observeInfo() {
        binding.apply {
            settingsUtility.apply {
                etMosqueName.setText(mainViewModel.masjidInfo.value?.masjidName)
                etJumma.setText(mainViewModel.masjidInfo.value?.jumua)
                etBottomMessage.setText(mainViewModel.masjidInfo.value?.bottomMessage)
                etLatitude.setText(mainViewModel.masjidInfo.value?.latitude)
                etLongitude.setText(mainViewModel.masjidInfo.value?.longitude)
                etCountry.setText(mainViewModel.masjidInfo.value?.country)
                etCity.setText(mainViewModel.masjidInfo.value?.city)

                Glide.with(requireContext()).load(mainViewModel.masjidInfo.value?.image)
                    .placeholder(R.drawable.ic_round_add_photo)
                    .error(R.drawable.ic_round_error)
                    .into(binding.imageView)
            }
        }
    }

    var selecteduri: Uri? = null
    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        if (requestCode == 120 && resultCode == Activity.RESULT_OK && data != null) {
            selecteduri = data.data
//            mainViewModel.imgMosque.postValue(compressImage(requireContext(), selecteduri))
            mainViewModel.imgMosque.postValue(selecteduri.toString())

        }

    }

    private fun requestPermissions() {
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
                        startActivityForResult(intent, 120)

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


    fun showConfirmDialog(masjidInfo: MasjidInfo?, onConfirm: (confirmed: Boolean) -> Unit) {
        val builder = AlertDialog.Builder(requireContext())
        builder.setTitle("Do you want to register this Mosque")
        builder.setMessage(masjidInfo.toString().replace(",\"", "\n"))

        builder.setPositiveButton("REGISTER") { dialog, p1 ->
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