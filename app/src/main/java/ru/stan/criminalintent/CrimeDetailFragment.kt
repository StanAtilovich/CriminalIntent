package ru.stan.criminalintent

import android.app.AlertDialog
import android.content.Intent
import android.content.pm.PackageManager
import android.content.pm.ResolveInfo
import android.net.Uri
import android.os.Bundle
import android.provider.ContactsContract
import android.text.format.DateFormat
import android.util.Log
import android.view.LayoutInflater
import android.view.Menu
import android.view.MenuInflater
import android.view.MenuItem
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.activity.OnBackPressedCallback
import androidx.activity.result.contract.ActivityResultContracts
import androidx.core.widget.doOnTextChanged
import androidx.fragment.app.Fragment
import androidx.fragment.app.setFragmentResultListener
import androidx.fragment.app.viewModels
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.lifecycleScope
import androidx.lifecycle.repeatOnLifecycle
import androidx.navigation.fragment.findNavController
import androidx.navigation.fragment.navArgs
import kotlinx.coroutines.launch
import ru.stan.criminalintent.databinding.FragmentCrimeDetailBinding
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale
import java.util.UUID


private const val TAG = "CrimeDetailFragment"
private const val DATE_FORMAT = "EEE, MMM, dd"

class CrimeDetailFragment : Fragment() {
    private var _binding: FragmentCrimeDetailBinding? = null
    private val binding
        get() = checkNotNull(_binding) {
            "Cannot access binding because it is null. Is the view visible?"
        }

    private val args: CrimeDetailFragmentArgs by navArgs()

    private val crimeDetailViewModel: CrimeDetailViewModel by viewModels {
        CrimeDetailViewModel.CrimeDetailViewModelFactory(args.crimeId)
    }

    private val selectSuspect = registerForActivityResult(
        ActivityResultContracts.PickContact()
    ) { uri: Uri? ->
        uri?.let { parseContactSelection(it) }
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        val crime = Crime(
            id = UUID.randomUUID(),
            title = "",
            date = Date(),
            isSolved = false
        )
        Log.d(TAG, "The crime ID is: ${args.crimeId}")
        setHasOptionsMenu(true)
    }

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding =
            FragmentCrimeDetailBinding.inflate(layoutInflater, container, false)
        return binding.root
    }


    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        val callback = object : OnBackPressedCallback(true) {
            override fun handleOnBackPressed() {
                if (binding.crimeTitle.text.toString().isEmpty()) {
                    Toast.makeText(
                        requireContext(),
                        R.string.write_crime,
                        Toast.LENGTH_SHORT
                    ).show()
                } else {
                    remove()
                    requireActivity().onBackPressedDispatcher.onBackPressed()
                }
            }
        }
        requireActivity().onBackPressedDispatcher.addCallback(viewLifecycleOwner, callback)


        binding.apply {
            crimeTitle.doOnTextChanged { text, _, _, _ ->
                crimeDetailViewModel.updateCrime { oldCrime ->
                    oldCrime.copy(title = text.toString())
                }
            }


            crimeSolved.setOnCheckedChangeListener { _, isChecked ->
                crimeDetailViewModel.updateCrime { oldCrime ->
                    oldCrime.copy(isSolved = isChecked)
                }
            }

            crimeSuspect.setOnClickListener {
                selectSuspect.launch(null)
            }
            val selectSuspectIntent = selectSuspect.contract.createIntent(
                requireContext(),
                null
            )
            crimeSuspect.isEnabled = canResolvelIntent(selectSuspectIntent)

        }

        viewLifecycleOwner.lifecycleScope.launch {
            viewLifecycleOwner.lifecycle.repeatOnLifecycle(Lifecycle.State.STARTED) {
                crimeDetailViewModel.crime.collect { crime ->
                    crime?.let { updateUI(it) }
                }
            }
        }

        setFragmentResultListener(
            DatePickerFragment.REQUEST_KEY_DATE
        ) { _, bundle ->
            val newDate =
                bundle.getSerializable(DatePickerFragment.BUNDLE_KEY_DATE) as Date
            crimeDetailViewModel.updateCrime { it.copy(date = newDate) }
        }

        setFragmentResultListener(
            TimePickerFragment.REQUEST_KEY_TIME
        ) { _, bundle ->
            val newTime =
                bundle.getSerializable(TimePickerFragment.BUNDLE_KEY_TIME) as Date
            crimeDetailViewModel.updateCrime { it.copy(date = newTime) }
        }


    }

    override fun onCreateOptionsMenu(menu: Menu, inflater: MenuInflater) {
        super.onCreateOptionsMenu(menu, inflater)
        inflater.inflate(R.menu.fragment_crime_detail, menu)
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        return when (item.itemId) {
            R.id.delete_crime -> {
                deleteCrime(args.crimeId)
                findNavController().popBackStack()
                true
            }

            else -> super.onContextItemSelected(item)
        }
    }

    private fun deleteCrime(id: UUID) {
        viewLifecycleOwner.lifecycleScope.launch {
            val crime = crimeDetailViewModel.getCrime(id)
            crimeDetailViewModel.deleteCrime(crime)
        }
        Toast.makeText(requireContext(), "Crime deleted.", Toast.LENGTH_SHORT).show()
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }

    private fun updateUI(crime: Crime) {
        binding.apply {
            if (crimeTitle.text.toString() != crime.title) {
                crimeTitle.setText(crime.title)
            }
            crimeDate.text = crime.date.toString()
            crimeDate.setOnClickListener {
                findNavController().navigate(
                    CrimeDetailFragmentDirections.selectDate(crime.date)
                )
            }
            crimeTime.text = formatDate(crime.date, "HH:mm zz")
            crimeTime.setOnClickListener {
                findNavController().navigate(
                    CrimeDetailFragmentDirections.selectTime(crime.date)
                )
            }

            crimeSolved.isChecked = crime.isSolved

            crimeReport.setOnClickListener {
                val reportIntent = Intent(Intent.ACTION_SEND).apply {
                    type = "text/plain"
                    putExtra(Intent.EXTRA_TEXT, getCrimeReport(crime))
                    putExtra(
                        Intent.EXTRA_SUBJECT,
                        getString(R.string.crime_report_suspect)
                    )
                }
                val chooserIntent = Intent.createChooser(
                    reportIntent, getString(R.string.send_report)
                )
                startActivity(chooserIntent)
            }

            crimeSuspect.text = crime.suspect.ifEmpty {
                getString(R.string.crime_suspect_text)
            }

            call.setOnClickListener {
                val contactUri = ContactsContract.CommonDataKinds.Phone.CONTENT_URI

                val projection = arrayOf(
                    ContactsContract.CommonDataKinds.Phone.DISPLAY_NAME,
                    ContactsContract.CommonDataKinds.Phone.NUMBER
                )

                val cursor = context?.contentResolver!!.query(
                    contactUri,
                    projection,
                    null,
                    null,
                    null
                )

                cursor?.use {
                    val contracts = mutableListOf<Pair<String, String>>()

                    while (it.moveToNext()){
                        val nameIndex = it.getColumnIndex(
                            ContactsContract.CommonDataKinds.Phone.DISPLAY_NAME
                        )
                        val name = it.getString(nameIndex)
                        val phoneNumberIndex = it.getColumnIndex(
                            ContactsContract.CommonDataKinds.Phone.NUMBER
                        )
                        val phoneNumber = it.getString(phoneNumberIndex)
                        contracts.add(Pair(name , phoneNumber))
                    }

                    val items = contracts.map { "${it.first}: ${it.second}" }.toTypedArray()

                    AlertDialog.Builder(context)
                        .setTitle("Выбери номер")
                        .setItems(items) {dialog, index ->
                            val selectPhonenumber = contracts[index].second

                            val intent = Intent(Intent.ACTION_DIAL)
                            intent.data = Uri.parse("tel: $selectPhonenumber")
                            startActivity(intent)
                            dialog.dismiss()
                        }
                        .setNegativeButton("Отмена") {dialog, _ ->
                            dialog.dismiss()
                        }
                        .show()
                }

                cursor?.close()
            }

        }
    }

    private fun formatDate(date: Date, pattern: String): String {
        val formatter = SimpleDateFormat(pattern, Locale.getDefault())
        return formatter.format(date)
    }

    private fun getCrimeReport(crime: Crime): String {
        val solvedString = if (crime.isSolved) {
            getString(R.string.crime_report_solved)
        } else {
            getString(R.string.crime_report_unsolved)
        }

        val dateString = DateFormat.format(DATE_FORMAT, crime.date).toString()
        val suspectText = if (crime.suspect.isBlank()) {
            getString(R.string.crime_report_no_suspect)
        } else {
            getString(R.string.crime_report_suspect, crime.suspect)
        }

        return getString(
            R.string.crime_report,
            crime.title, dateString, solvedString, suspectText
        )
    }

    private fun parseContactSelection(contactUri: Uri) {
        val queriFields = arrayOf(ContactsContract.Contacts.DISPLAY_NAME)

        val queriCursor = requireActivity().contentResolver
            .query(contactUri, queriFields, null, null, null)

        queriCursor?.use { cursor ->
            if (cursor.moveToFirst()) {
                val suspect = cursor.getString(0)
                crimeDetailViewModel.updateCrime { oldCrime ->
                    oldCrime.copy(suspect = suspect)
                }
            }
        }
    }

    private fun canResolvelIntent(intent: Intent): Boolean {
        // intent.addCategory(Intent.CATEGORY_HOME) закрывает наше намериние и кнопка не высвечивается
        val peckageManager: PackageManager = requireActivity().packageManager
        val resolvedActivity: ResolveInfo? =
            peckageManager.resolveActivity(
                intent,
                PackageManager.MATCH_DEFAULT_ONLY
            )
        return resolvedActivity != null
    }
}