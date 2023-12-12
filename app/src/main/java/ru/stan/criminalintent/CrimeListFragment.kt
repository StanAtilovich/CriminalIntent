package ru.stan.criminalintent

import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.recyclerview.widget.LinearLayoutManager
import ru.stan.criminalintent.databinding.FragmentCrimeListBinding

private const val TAG = "CrimeListFragment"

class CrimeListFragment : Fragment() {
    private var _binding: FragmentCrimeListBinding? = null
    private val binding
        get() = checkNotNull(_binding) {
            "Cannot access binding because it is null/ Is the view visible?"
        }
    private val crimeListViewModel: CrimeListViewModel by viewModels()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        Log.d(TAG, "Total crimes: ${crimeListViewModel.crimes.size}")
    }

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentCrimeListBinding.inflate(inflater, container, false)

        binding.crimeRecyclewView.layoutManager = LinearLayoutManager(context)

        val crimes = crimeListViewModel.crimes
      // val adapter = CrimeSeriousAdapter(crimes)
         val adapter = CrimeListAdapter(crimes)
        binding.crimeRecyclewView.adapter = adapter
        return binding.root
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}