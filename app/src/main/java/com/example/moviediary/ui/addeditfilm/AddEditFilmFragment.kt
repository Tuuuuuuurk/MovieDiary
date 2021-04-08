package com.example.moviediary.ui.addeditfilm

import android.Manifest
import android.app.Activity
import android.app.DatePickerDialog
import android.content.Intent
import android.content.pm.PackageManager
import android.graphics.drawable.LayerDrawable
import android.os.Build
import android.os.Bundle
import android.provider.MediaStore
import android.view.View
import android.widget.AdapterView
import android.widget.AdapterView.OnItemSelectedListener
import androidx.activity.result.contract.ActivityResultContracts
import androidx.annotation.RequiresApi
import androidx.core.content.ContextCompat
import androidx.core.os.bundleOf
import androidx.core.widget.addTextChangedListener
import androidx.fragment.app.Fragment
import androidx.fragment.app.setFragmentResult
import androidx.fragment.app.viewModels
import androidx.lifecycle.lifecycleScope
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.LinearLayoutManager
import coil.load
import com.example.moviediary.R
import com.example.moviediary.databinding.FilmEditingViewBinding
import com.example.moviediary.ui.PERMISSION_CODE
import com.google.android.material.snackbar.Snackbar
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.flow.collect
import java.text.SimpleDateFormat
import java.time.LocalDateTime
import java.time.ZoneId
import java.util.*

@AndroidEntryPoint
class AddEditFilmFragment : Fragment(R.layout.film_editing_view) {

    private val addEditFilmViewModel: AddEditFilmViewModel by viewModels()

    private val producersAdapter: ProducersListAdapter = ProducersListAdapter()

    private val calendar = Calendar.getInstance()

    @RequiresApi(Build.VERSION_CODES.O)
    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        val binding = FilmEditingViewBinding.bind(view)

        binding.apply {

            producersList.apply {
                adapter = producersAdapter
                layoutManager = LinearLayoutManager(requireContext())
                setHasFixedSize(true)
            }

            if(addEditFilmViewModel.filmStatus == "Буду смотреть")
                spinnerView.setSelection(0)
            else
                spinnerView.setSelection(1)
            addEditFilmNameView.setText(addEditFilmViewModel.filmName)
            addEditFilmImageView.load(addEditFilmViewModel.filmPoster)
            addEditFilmGenreView.setText(addEditFilmViewModel.filmGenre)
            addEditFilmDateView.text = addEditFilmViewModel.filmDate

            val stars = ratingBar.progressDrawable as LayerDrawable
            stars.setTint(resources.getColor(R.color.star_color))
            ratingBar.rating = addEditFilmViewModel.film?.rating?.toFloat() ?: 0.0f
            addEditFilmRatingView.text = addEditFilmViewModel.filmRating.toString()

            addEditFilmNameView.addTextChangedListener {
                addEditFilmViewModel.filmName = it.toString()
            }

            addEditFilmGenreView.addTextChangedListener {
                addEditFilmViewModel.filmGenre = it.toString()
            }

            addEditFilmDateView.setOnClickListener {
                DatePickerDialog(requireContext(), dateSetListener,
                        addEditFilmViewModel.dateYear,
                        addEditFilmViewModel.dateMonth-1,
                        addEditFilmViewModel.dateDay).show()
            }

            addEditFilmImageView.setOnClickListener {
                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
                    if (context?.let { it1 -> ContextCompat.checkSelfPermission(it1.applicationContext, Manifest.permission.READ_EXTERNAL_STORAGE) } ==
                            PackageManager.PERMISSION_DENIED) {
                        val permissions = arrayOf(Manifest.permission.READ_EXTERNAL_STORAGE)
                        requestPermissions(permissions, PERMISSION_CODE)
                        if (context?.let { it1 -> ContextCompat.checkSelfPermission(it1.applicationContext, Manifest.permission.READ_EXTERNAL_STORAGE) } !=
                                PackageManager.PERMISSION_DENIED)
                            openGalleryForImage()
                    }
                    else
                        openGalleryForImage()
                }
                else
                    openGalleryForImage()
            }

            spinnerView.onItemSelectedListener = object : OnItemSelectedListener {
                override fun onItemSelected(parent: AdapterView<*>?, itemSelected: View?, selectedItemPosition: Int, selectedId: Long) {
                    if(itemSelected != null) {
                        addEditFilmViewModel.filmStatus = resources.getStringArray(R.array.status)[selectedItemPosition]
                        if (addEditFilmViewModel.filmStatus == "Буду смотреть") {
                            ratingBar.rating = 0.0f
                            addEditFilmRatingView.text = ratingBar.rating.toInt().toString()
                            addEditFilmViewModel.filmRating = 0
                        }
                    }
                }
                override fun onNothingSelected(parent: AdapterView<*>?) {}
            }

            addProducerButton.setOnClickListener {
                addEditFilmViewModel.onAddProducerClick()
            }

            ratingBar.setOnRatingBarChangeListener { _, rating, _ ->
                addEditFilmViewModel.filmRating = rating.toInt()
                addEditFilmRatingView.text = rating.toInt().toString()
                if( rating.toInt() != 0) {
                    spinnerView.setSelection(1)
                    addEditFilmViewModel.filmStatus = resources.getStringArray(R.array.status)[1]
                }
            }

            fabAddNote.setOnClickListener {
                addEditFilmViewModel.onAddFilmClick()
            }
        }

        producersAdapter.setProducers(addEditFilmViewModel.filmProducers)

        viewLifecycleOwner.lifecycleScope.launchWhenStarted {
            addEditFilmViewModel.addEditFilmEvent.collect { event ->
                when (event) {
                    is AddEditFilmViewModel.AddEditFilmEvent.ShowInvalidInputMessage -> {
                        Snackbar.make(requireView(), event.msg, Snackbar.LENGTH_LONG).show()
                    }
                    is AddEditFilmViewModel.AddEditFilmEvent.NavigateBackWithResult -> {
                        binding.addEditFilmNameView.clearFocus()
                        binding.addEditFilmGenreView.clearFocus()
                        setFragmentResult("add_edit_request",
                                bundleOf("add_edit_result" to event.result)
                        )
                        findNavController().popBackStack()
                    }
                    is AddEditFilmViewModel.AddEditFilmEvent.ChangeDateView -> {
                        binding.addEditFilmDateView.text =  addEditFilmViewModel.filmDate
                    }
                    is AddEditFilmViewModel.AddEditFilmEvent.UpdateProducersListView -> {
                        producersAdapter.notifyDataSetChanged()
                    }
                    is AddEditFilmViewModel.AddEditFilmEvent.UpdatePosterView -> {
                        binding.addEditFilmImageView.load(addEditFilmViewModel.filmPoster)
                    }
                }
            }
        }
    }

    @RequiresApi(Build.VERSION_CODES.O)
    private val dateSetListener = DatePickerDialog.OnDateSetListener { _, year, monthOfYear, dayOfMonth ->
        calendar.set(Calendar.YEAR, year)
        calendar.set(Calendar.MONTH, monthOfYear)
        calendar.set(Calendar.DAY_OF_MONTH, dayOfMonth)

        val sdf = SimpleDateFormat("dd MMMM yyyy", Locale.getDefault())
        addEditFilmViewModel.filmDate = sdf.format(calendar.time)
        addEditFilmViewModel.dateYear = year
        addEditFilmViewModel.dateMonth = monthOfYear
        addEditFilmViewModel.dateDay = dayOfMonth
        addEditFilmViewModel.dateLong =
                LocalDateTime.of(year, monthOfYear+1, dayOfMonth, 0, 0, 0)
                .atZone(ZoneId.systemDefault()).toInstant().toEpochMilli()
        addEditFilmViewModel.onDateChanged()
    }

    private fun openGalleryForImage() {
        val intent = Intent(Intent.ACTION_PICK, MediaStore.Images.Media.EXTERNAL_CONTENT_URI)
        intent.type = "image/*"
        resultLauncher.launch(intent)
    }

    private val resultLauncher = registerForActivityResult(ActivityResultContracts.StartActivityForResult()) { result ->
        if (result.resultCode == Activity.RESULT_OK) {
            val data: Intent? = result.data
            val bitmapUri = data?.data
            if (bitmapUri != null)
                addEditFilmViewModel.onChoosePosterFromGallery(MediaStore.Images.Media.getBitmap(requireContext().contentResolver, bitmapUri))
        }
    }
}