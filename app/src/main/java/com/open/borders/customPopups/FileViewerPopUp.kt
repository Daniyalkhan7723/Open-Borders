package com.open.borders.customPopups

import android.graphics.Color
import android.graphics.drawable.ColorDrawable
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.DialogFragment
import com.bumptech.glide.Glide
import com.open.borders.R
import com.open.borders.databinding.FileViewerLayoutBinding
import com.open.borders.utils.SharePreferenceHelper


class FileViewerPopUp : DialogFragment() {

    private lateinit var binding: FileViewerLayoutBinding

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        binding = FileViewerLayoutBinding.bind(view)
        requireDialog().window?.setBackgroundDrawableResource(R.drawable.progress_dialog_rounded_bg)
        requireDialog().window?.setBackgroundDrawable(ColorDrawable(Color.parseColor("#99000000")))

        binding.cancelImage.setOnClickListener {
            dialog?.dismiss()
        }
    }

    override fun onStart() {
        super.onStart()
        requireDialog().window?.setLayout(
            ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.MATCH_PARENT
        )
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?
    ): View? {
        val view = layoutInflater.inflate(R.layout.file_viewer_layout, container, false)
        binding = FileViewerLayoutBinding.bind(view)

        val fileName = SharePreferenceHelper.getInstance(requireContext()).getFileName()
        Glide.with(requireContext()).load(fileName)
            .into(binding.imageFileId)
        return view
    }

}