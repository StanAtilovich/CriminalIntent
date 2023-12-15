package ru.stan.criminalintent

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.lifecycleScope
import androidx.lifecycle.repeatOnLifecycle
import androidx.recyclerview.widget.LinearLayoutManager
import kotlinx.coroutines.launch
import ru.stan.criminalintent.databinding.FragmentCrimeListBinding


private const val TAG = "CrimeListFragment"

// Make CrimeListFragment a subclass of Fragment (androidx.fragment.app.Fragment)
class CrimeListFragment : Fragment() {

    private var _binding: FragmentCrimeListBinding? = null
    private val binding
        get() = checkNotNull(_binding) {
            "Cannot access binding because it is null. Is the view visible?"
        }

    private val crimeListViewModel: CrimeListViewModel by viewModels()

    // 12.5 Create a variable for an instance of the Job class
    //(12.6) private var job: Job? = null

    /* 12.7 Clean up code: Don't need to log the number of crimes anymore
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        Log.d(TAG, "Total crimes: ${crimeListViewModel.crimes.size}") // Logs the number of crimes found in CrimeListViewModel
    }*/

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        _binding = FragmentCrimeListBinding.inflate(inflater, container, false)

        // Set up the LayoutManager
        binding.crimeRecyclerView.layoutManager = LinearLayoutManager(context)

        /* 12.7 Clean up code: delete the code that tries to initialize CrimeListAdapter with missing data
        // Instantiate an instance of CrimeListAdapter with our crime data AND connect it to the RecyclerView
        val crimes = crimeListViewModel.crimes
        val adapter = CrimeListAdapter(crimes)
        binding.crimeRecyclerView.adapter = adapter*/

        return binding.root
    }

    /* 12.6 Removed code below to use repeatOnLifecycle
    // 12.5 call the coroutine by using job
    override fun onStart() {
        super.onStart()
        job = viewLifecycleOwner.lifecycleScope.launch {
            val crimes = crimeListViewModel.loadCrimes()
            binding.crimeRecyclerView.adapter = CrimeListAdapter(crimes)
        }
    }
    // 12.5 End the coroutine by using job
    override fun onStop() {
        super.onStop()
        job?.cancel()
    }*/

    // 12.6 Implement repeatOnLifecycle
    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        viewLifecycleOwner.lifecycleScope.launch {
            viewLifecycleOwner.repeatOnLifecycle(Lifecycle.State.STARTED) {
                //(12.27)val crimes = crimeListViewModel.loadCrimes()
                // 12.27 Replace call to .loadCrimes() with a collect {} function call on the crimes property
                crimeListViewModel.crimes.collect { crimes ->
                    binding.crimeRecyclerView.adapter =
                        CrimeListAdapter(crimes)
                }
            }
            // NOTE: Don't need to cancel Job bc repeatOnLifecyle will handle that
        }
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}