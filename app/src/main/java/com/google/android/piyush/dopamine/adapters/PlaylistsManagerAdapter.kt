package com.google.android.piyush.dopamine.adapters

import android.content.Context
import android.content.Intent
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.core.content.edit
import androidx.fragment.app.FragmentManager
import androidx.recyclerview.widget.RecyclerView
import com.google.android.material.bottomsheet.BottomSheetDialogFragment
import com.google.android.material.card.MaterialCardView
import com.google.android.material.dialog.MaterialAlertDialogBuilder
import com.google.android.material.textview.MaterialTextView
import com.google.android.piyush.database.model.CustomPlaylistView
import com.google.android.piyush.database.viewModel.DatabaseViewModel
import com.google.android.piyush.dopamine.R
import com.google.android.piyush.dopamine.activities.DopamineHome
import com.google.android.piyush.dopamine.databinding.BottomSheetPlaylistBinding
import com.google.android.piyush.dopamine.utilities.ToastUtilities
import com.google.android.piyush.dopamine.utilities.dopamineSharedPreferences

class PlaylistsManagerAdapter(
    val context : Context,
    private val  playlistList : List<CustomPlaylistView>?,
    val fragment : FragmentManager
) : RecyclerView.Adapter<PlaylistsManagerViewHolder>() {
    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): PlaylistsManagerViewHolder {
        return PlaylistsManagerViewHolder(
            LayoutInflater.from(parent.context).inflate(R.layout.item_manager_playlists, parent, false)
        )
    }
    override fun getItemCount(): Int = playlistList?.size!!

    override fun onBindViewHolder(holder: PlaylistsManagerViewHolder, position: Int) {
        holder.playlistName.text = playlistList?.get(position)?.playListName
        holder.playlistDescription.text = playlistList?.get(position)?.playListDescription
        if(playlistList?.get(position)?.playListName.equals("favorite playlist") || playlistList?.get(position)?.playListName.equals("watch later")){
            holder.editPlaylist.visibility = View.GONE
            holder.deletePlaylist.visibility = View.GONE
        }
        holder.editPlaylist.setOnClickListener {
            val managerPlaylistsBottomSheet = ManagerPlaylistsBottomSheet()
            managerPlaylistsBottomSheet.show(fragment, managerPlaylistsBottomSheet.tag)
            dopamineSharedPreferences(context).edit {
                putString("playlistName", playlistList?.get(position)?.playListName)
                putString("playlistDescription", playlistList?.get(position)?.playListDescription)
            }
        }
        holder.deletePlaylist.setOnClickListener {
            val databaseViewModel = DatabaseViewModel(context)
            MaterialAlertDialogBuilder(context).apply {
                setTitle("Delete Playlist")
                setIcon(R.drawable.delete)
                setMessage("Are you sure you want to delete this playlist? This action cannot be undone.")
                setPositiveButton("Yes") { dialog, _ ->
                    ToastUtilities.showToast(context, "Playlist Deleted")
                    databaseViewModel.deletePlaylist(playlistList?.get(position)?.playListName!!)
                    dialog.dismiss()
                    context.startActivity(
                            Intent(
                                context,
                                DopamineHome::class.java
                            ).putExtra(
                                "fromPlaylistManager",true
                            ).addFlags(
                                Intent.FLAG_ACTIVITY_CLEAR_TOP
                            ).setFlags(
                                Intent.FLAG_ACTIVITY_NEW_TASK
                            )
                        )
                }
                setNegativeButton("No") {
                        dialog, _ ->
                    dialog.dismiss()
                }
                setCancelable(true)
                create()
                show()
            }
        }
    }
}


class PlaylistsManagerViewHolder(view : View) : RecyclerView.ViewHolder(view){
    val playlistName : MaterialTextView = view.findViewById(R.id.playlistName)
    val playlistDescription : MaterialTextView = view.findViewById(R.id.playlistDescription)
    val editPlaylist : MaterialCardView = view.findViewById(R.id.editPlaylist)
    val deletePlaylist : MaterialCardView = view.findViewById(R.id.deletePlaylist)
}

class ManagerPlaylistsBottomSheet : BottomSheetDialogFragment() {

    private var modalBottomSheet: BottomSheetPlaylistBinding? = null
    private lateinit var databaseViewModel: DatabaseViewModel

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? = inflater.inflate(R.layout.bottom_sheet_playlist, container, false)

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        val binding = BottomSheetPlaylistBinding.bind(view)
        modalBottomSheet = binding
        databaseViewModel = DatabaseViewModel(requireContext())

        val oldPlaylistName = dopamineSharedPreferences(requireContext()).getString("playlistName", "")
        val playlistDescription = dopamineSharedPreferences(requireContext()).getString("playlistDescription", "")
        binding.playlistName.setText(oldPlaylistName)
        binding.playlistDescription.setText(playlistDescription)

        binding.addPlaylist.setOnClickListener {
            if(databaseViewModel.isPlaylistExist(binding.playlistName.text.toString())){
                binding.playlistNameInputLayout.isErrorEnabled = true
                binding.playlistNameInputLayout.error = "Playlist Already Exists"
            }else{
                if(oldPlaylistName?.isEmpty()!!.equals(true)){
                    ToastUtilities.showToast(context, "Please Fill All Fields")
                }else {
                    if(oldPlaylistName.toString() == binding.playlistName.text.toString()){
                        ToastUtilities.showToast(context, "Playlist Name Not Changed ❌")
                    }else {
                        databaseViewModel.updatePlaylistName(
                            oldPlaylistName.toString(),
                            binding.playlistName.text.toString(),
                            binding.playlistDescription.text.toString()
                        )
                        ToastUtilities.showToast(context, "$oldPlaylistName Updated ✅")
                        this.dismiss()
                        startActivity(
                            Intent(
                                requireContext(),
                                DopamineHome::class.java
                            ).putExtra(
                                "fromPlaylistManager",true
                            ).addFlags(
                                Intent.FLAG_ACTIVITY_CLEAR_TOP
                            ).setFlags(
                                Intent.FLAG_ACTIVITY_NEW_TASK
                            )
                        )
                    }
                }
            }
        }
    }

    override fun onDestroyView() {
        super.onDestroyView()
        modalBottomSheet = null
    }
}