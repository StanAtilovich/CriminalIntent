package ru.stan.criminalintent

import android.content.Intent
import android.net.Uri
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.recyclerview.widget.RecyclerView
import ru.stan.criminalintent.databinding.ListItemCrimeBinding
import ru.stan.criminalintent.databinding.ListItemSeriousCrimeBinding
import java.text.SimpleDateFormat
import java.util.Locale


class CrimeHolder(
    val binding: ListItemCrimeBinding
) : RecyclerView.ViewHolder(binding.root) {
    fun bind(crime: Crime) {
        binding.crimeTitle.text = crime.title
        binding.crimeDate.text = crime.data.toString()

        binding.root.setOnClickListener {
            Toast.makeText(
                binding.root.context,
                "${crime.title} clicked",
                Toast.LENGTH_SHORT
            ).show()
        }
    }
}

class SeriousCrimeHolder(
    val binding: ListItemSeriousCrimeBinding
) : RecyclerView.ViewHolder(binding.root) {

    private val dataFormat =SimpleDateFormat("EEEE, MMMM d, yyyy", Locale.getDefault())

    fun bind(crime: Crime) {
        binding.crimeTitle.text = crime.title
        binding.crimeDate.text = dataFormat.format(crime.data)
        binding.contactPoliceButton.setOnClickListener {
            Toast.makeText(
                binding.root.context,
                "${crime.title} contact police!",
                Toast.LENGTH_SHORT
            ).show()
        }
        binding.root.setOnClickListener {
            Toast.makeText(
                binding.root.context,
                "${crime.title} clicked",
                Toast.LENGTH_SHORT
            ).show()
        }
    }
}

class CrimeSeriousAdapter(
    private val crimes: List<Crime>
) : RecyclerView.Adapter<SeriousCrimeHolder>() {
    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): SeriousCrimeHolder {
        val inflater = LayoutInflater.from(parent.context)
        val binding = ListItemSeriousCrimeBinding.inflate(inflater, parent, false)
        return SeriousCrimeHolder(binding)
    }

    override fun getItemCount(): Int = crimes.size

    override fun onBindViewHolder(holder: SeriousCrimeHolder, position: Int) {
        val crime = crimes[position]
        holder.apply {
            binding.crimeTitle.text = crime.title
            binding.crimeDate.text = crime.data.toString()

            binding.crimeSolved.visibility = if (crime.isSolved) {
                View.VISIBLE
            } else {
                View.GONE
            }
        }
        holder.bind(crime)
    }

}

class CrimeListAdapter(
    private val crimes: List<Crime>
) : RecyclerView.Adapter<CrimeHolder>() {
    override fun onCreateViewHolder(
        parent: ViewGroup,
        viewType: Int
    ): CrimeHolder {
        val inflater = LayoutInflater.from(parent.context)
        val binding = ListItemCrimeBinding.inflate(inflater, parent, false)
        return CrimeHolder(binding)
    }

    override fun onBindViewHolder(holder: CrimeHolder, position: Int) {
        val crime = crimes[position]
        holder.apply {
            binding.crimeTitle.text = crime.title
            binding.crimeDate.text = crime.data.toString()
        }
        holder.bind(crime)
    }

    override fun getItemCount() = crimes.size

}