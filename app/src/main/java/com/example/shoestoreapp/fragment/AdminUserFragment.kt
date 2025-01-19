import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.EditText
import androidx.fragment.app.Fragment
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import android.app.AlertDialog
import android.text.Editable
import android.text.TextWatcher
import android.widget.Toast
import com.example.shoestoreapp.R
import com.example.shoestoreapp.adapter.UserAdapter
import com.example.shoestoreapp.data.model.User
import com.google.firebase.firestore.FirebaseFirestore

class AdminUserFragment : Fragment() {
    private lateinit var recyclerView: RecyclerView
    private lateinit var searchEditText: EditText
    private lateinit var userAdapter: UserAdapter
    private var userList = mutableListOf<User>()
    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        return inflater.inflate(R.layout.fragment_admin_user, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        recyclerView = view.findViewById(R.id.recyclerViewUsers)
        searchEditText = view.findViewById(R.id.editTextSearch)

        setupRecyclerView()
        setupSearch()
        fetchUsers()
    }

    private fun setupRecyclerView() {
        userAdapter = UserAdapter { user ->
            showDeleteConfirmationDialog(user)
        }
        recyclerView.apply {
            layoutManager = LinearLayoutManager(context)
            adapter = userAdapter
        }
    }

    private fun setupSearch() {
        searchEditText.addTextChangedListener(object : TextWatcher {
            override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {}
            override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {}
            override fun afterTextChanged(s: Editable?) {
                filterUsers(s.toString())
            }
        })
    }

    private fun filterUsers(query: String) {
        if (query.isBlank()) {
            userAdapter.submitList(userList)
            return
        }

        val filteredList = userList.filter { user ->
            user.username.contains(query, ignoreCase = true) ||
                    user.email.contains(query, ignoreCase = true)
        }

        userAdapter.submitList(filteredList)
    }


    private fun fetchUsers() {
        FirebaseFirestore.getInstance()
            .collection("users")
            .addSnapshotListener { snapshot, e ->
                if (e != null) {
                    Toast.makeText(context, "Error loading users", Toast.LENGTH_SHORT).show()
                    return@addSnapshotListener
                }

                userList = snapshot?.documents?.mapNotNull { doc ->
                    doc.toObject(User::class.java)?.copy(id = doc.id)
                }?.toMutableList() ?: mutableListOf()

                userAdapter.submitList(userList)
            }
    }


    private fun showDeleteConfirmationDialog(user: User) {
        AlertDialog.Builder(requireContext())
            .setTitle("Delete User")
            .setMessage("Are you sure you want to delete ${user.username}?")
            .setPositiveButton("Delete") { _, _ ->
                deleteUser(user)
            }
            .setNegativeButton("Cancel", null)
            .show()
    }

    private fun deleteUser(user: User) {
        FirebaseFirestore.getInstance()
            .collection("users")
            .document(user.id)
            .delete()
            .addOnSuccessListener {
                Toast.makeText(context, "User deleted successfully", Toast.LENGTH_SHORT).show()
            }
            .addOnFailureListener {
                Toast.makeText(context, "Failed to delete user", Toast.LENGTH_SHORT).show()
            }
    }
}