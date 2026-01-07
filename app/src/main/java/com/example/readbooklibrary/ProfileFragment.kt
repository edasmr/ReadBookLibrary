package com.example.readbooklibrary

import android.Manifest
import android.app.Activity
import android.content.Intent
import android.content.pm.PackageManager
import android.net.Uri
import android.os.Build
import android.os.Bundle
import androidx.appcompat.app.AlertDialog
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.activity.result.contract.ActivityResultContracts
import androidx.core.content.ContextCompat
import androidx.core.content.edit
import com.bumptech.glide.Glide
import com.example.readbooklibrary.databinding.FragmentProfileBinding

class ProfileFragment : Fragment() {

    private lateinit var binding: FragmentProfileBinding
    private var selectedImageUri: Uri? = null

    private val pickImageLauncher = registerForActivityResult(
        ActivityResultContracts.StartActivityForResult()
    ) { result ->
        if (result.resultCode == Activity.RESULT_OK) {
            result.data?.data?.let { uri ->
                selectedImageUri = uri
                Glide.with(this)
                    .load(uri)
                    .circleCrop()
                    .into(binding.profileImageView)
                saveProfileImage(uri)
            }
        }
    }

    private val requestPermissionLauncher = registerForActivityResult(
        ActivityResultContracts.RequestPermission()
    ) { isGranted ->
        if (isGranted) {
            pickImageFromGallery()
        } else {
            AlertDialog.Builder(requireContext())
                .setTitle("İzin gerekli")
                .setMessage("Profil fotoğrafı seçebilmek için galeri izni gerekiyor.")
                .setPositiveButton("Tamam") { dialog, _ -> dialog.dismiss() }
                .show()
        }
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        binding = FragmentProfileBinding.inflate(inflater, container, false)

        setupCardInteractions()
        loadCardInfo()
        loadProfileImage()

        if (isCardEmpty()) enableEditMode(true) else enableEditMode(false)

        binding.editBtn.setOnClickListener { enableEditMode(true) }
        binding.deletebtn.setOnClickListener { deleteCardInfo() }

        // Resim seçme butonu
        binding.selectImageButton.setOnClickListener { checkGalleryPermissionAndOpen() }

        return binding.root
    }
    private fun checkGalleryPermissionAndOpen() {
        val permission = if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
            Manifest.permission.READ_MEDIA_IMAGES
        } else {
            Manifest.permission.READ_EXTERNAL_STORAGE
        }

        val isGranted = ContextCompat.checkSelfPermission(
            requireContext(),
            permission
        ) == PackageManager.PERMISSION_GRANTED

        if (isGranted) {
            pickImageFromGallery()
        } else {
            // Daha önce reddedilmiş mi kontrol edebiliriz (opsiyonel)
            requestPermissionLauncher.launch(permission)
        }
    }

    private fun pickImageFromGallery() {
        val intent = Intent(Intent.ACTION_PICK)
        intent.type = "image/*"
        pickImageLauncher.launch(intent)
    }

    private fun saveProfileImage(uri: Uri) {
        val sp = requireContext().getSharedPreferences("profileCard", 0)
        sp.edit { putString("profile_image_uri", uri.toString()) }
    }

    private fun loadProfileImage() {
        val sp = requireContext().getSharedPreferences("profileCard", 0)
        val uriString = sp.getString("profile_image_uri", null)
        uriString?.let {
            Glide.with(this)
                .load(Uri.parse(it))
                .circleCrop()
                .into(binding.profileImageView)
        }
    }

    private fun updateCardUI() {
        if (binding.card.isChecked) {
            binding.card.setCardBackgroundColor(requireContext().getColor(R.color.blue))
            binding.card.strokeWidth = 6
            binding.card.strokeColor = requireContext().getColor(R.color.black)
        } else {
            binding.card.setCardBackgroundColor(requireContext().getColor(com.google.android.material.R.color.design_default_color_background))
            binding.card.strokeColor = requireContext().getColor(R.color.black)
            binding.card.strokeWidth = 6
        }
    }

    private fun deleteCardInfo() {
        binding.cardTitle.setText("")
        binding.cardSubtitle.setText("")
        binding.cardDesc.setText("")
        val sp = requireContext().getSharedPreferences("profileCard", 0)
        sp.edit { clear() }
        enableEditMode(true)
        binding.cardTitle.requestFocus()
    }

    private fun enableEditMode(enable: Boolean) {
        binding.cardTitle.isEnabled = enable
        binding.cardSubtitle.isEnabled = enable
        binding.cardDesc.isEnabled = enable

        binding.cardTitle.isFocusableInTouchMode = enable
        binding.cardSubtitle.isFocusableInTouchMode = enable
        binding.cardDesc.isFocusableInTouchMode = enable

        binding.editBtn.text = if (enable) "Save" else "Edit"

        if (enable) {
            binding.editBtn.setOnClickListener { saveCardInfo() }
        } else {
            binding.editBtn.setOnClickListener { enableEditMode(true) }
        }
    }

    private fun saveCardInfo() {
        val title = binding.cardTitle.text.toString()
        val subtitle = binding.cardSubtitle.text.toString()
        val desc = binding.cardDesc.text.toString()
        val sp = requireContext().getSharedPreferences("profileCard", 0)
        sp.edit {
            putString("card_title", title)
            putString("card_subtitle", subtitle)
            putString("card_desc", desc)
        }
        enableEditMode(false)
    }

    private fun loadCardInfo() {
        val sp = requireContext().getSharedPreferences("profileCard", 0)
        binding.cardTitle.setText(sp.getString("card_title", ""))
        binding.cardSubtitle.setText(sp.getString("card_subtitle", ""))
        binding.cardDesc.setText(sp.getString("card_desc", ""))
    }

    private fun isCardEmpty(): Boolean {
        val sp = requireContext().getSharedPreferences("profileCard", 0)
        val title = sp.getString("card_title", "")
        val subtitle = sp.getString("card_subtitle", "")
        val desc = sp.getString("card_desc", "")
        return title.isNullOrEmpty() && subtitle.isNullOrEmpty() && desc.isNullOrEmpty()
    }

    private fun setupCardInteractions() {
        binding.card.setOnClickListener {
            binding.card.isChecked = !binding.card.isChecked
            updateCardUI()
        }

        binding.card.setOnLongClickListener {
            binding.card.isChecked = !binding.card.isChecked
            updateCardUI()
            true
        }
    }
}
