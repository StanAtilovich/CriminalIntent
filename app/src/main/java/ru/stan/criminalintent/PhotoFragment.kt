package ru.stan.criminalintent

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.core.net.toUri
import androidx.fragment.app.DialogFragment
import ru.stan.criminalintent.databinding.FragmentPhotoBinding
import java.io.File


private const val PHOTO_FILE_NAME = "PHOTO_FILE_NAME"
class PhotoFragment : DialogFragment() {
   private var _binding: FragmentPhotoBinding? = null
    private val binding get() = checkNotNull(_binding){
        "Cannot access binding because it is null. Is the view visible?"
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentPhotoBinding.inflate(inflater,container,false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        val photoFileName = arguments?.getString(PHOTO_FILE_NAME)

        val photoFile = photoFileName?.let {
            File(requireContext().applicationContext.filesDir,it)
        }
        binding.crimePhotoZoom.setImageURI(photoFile?.toUri())
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }

    companion object{
        fun newInstance(photoFilName: String): PhotoFragment{
            val frag = PhotoFragment()
            val args = Bundle()
            args.putSerializable(PHOTO_FILE_NAME,photoFilName)
            frag.arguments = args
            return frag
        }
    }
}
