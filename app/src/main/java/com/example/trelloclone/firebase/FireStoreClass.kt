package com.example.trelloclone.firebase

import android.app.Activity
import android.util.Log
import android.widget.Toast
import com.example.trelloclone.activities.*
import com.example.trelloclone.models.Board
import com.example.trelloclone.models.User
import com.example.trelloclone.utils.Constants
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.SetOptions

class FireStoreClass {

    // Create a instance of Firebase Firestore
    private val mFireStore = FirebaseFirestore.getInstance()

    /**
     * A function to make an entry of the registered user in the firestore database.
     */
    fun registerUser(activity: SignUpActivity, userInfo: User) {

        mFireStore.collection(Constants.USERS)
            // Document ID for users fields. Here the document it is the User ID.
            .document(getCurrentUserID())
            // Here the userInfo are Field and the SetOption is set to merge. It is for if we wants to merge
            .set(userInfo, SetOptions.merge())
            .addOnSuccessListener {

                // Here call a function of base activity for transferring the result to it.
                Log.e(activity.javaClass.simpleName, "User Created Successfully.")


                Toast.makeText(activity, "User Created Successfully", Toast.LENGTH_SHORT).show()
                activity.userRegisteredSuccess()
            }
            .addOnFailureListener { e ->
                activity.hideProgressDialog()
                Log.e(
                    activity.javaClass.simpleName,
                    "Error writing document",
                    e
                )
            }
    }

    fun createBoard(activity: CreateBoardActivity, board: Board) {
        mFireStore.collection(Constants.BOARDS).document()
            // Here the boardInfo are Field and the SetOption is set to merge. It is for if we wants to merge
            .set(board, SetOptions.merge())
            .addOnSuccessListener {
                // Here call a function of base activity for transferring the result to it.
                Log.e(activity.javaClass.simpleName, "Board Created Successfully.")
                Toast.makeText(activity, "Board Created Successfully", Toast.LENGTH_SHORT).show()
                activity.boardCreatedSuccessfully()
            }
            .addOnFailureListener { e ->
                activity.hideProgressDialog()
                Log.e(
                    activity.javaClass.simpleName,
                    "Error writing document for board",
                    e
                )
            }
    }

    fun getBoardList(activity: MainActivity) {
        mFireStore.collection(Constants.BOARDS)
            .whereArrayContains(Constants.ASSIGNED_TO, getCurrentUserID()).get()
            .addOnSuccessListener { document ->
                Log.i(activity.javaClass.simpleName, document.documents.toString())
                val boardsList: ArrayList<Board> = ArrayList()
                for (i in document) {
                    val board = i.toObject(Board::class.java)!!
                    board.documentId = i.id
                    boardsList.add(board)
                }
                activity.populateBoardsListToUI(boardsList)
            }.addOnFailureListener { e ->
                activity.hideProgressDialog()
                Log.e(
                    activity.javaClass.simpleName,
                    "Error while getting a board document",
                    e
                )
            }
    }

    fun updateUserProfileData(activity: MyProfileActivity, userHaspMap: HashMap<String, Any>) {
        mFireStore.collection(Constants.USERS)
            .document(getCurrentUserID()).update(userHaspMap).addOnSuccessListener {
                Toast.makeText(activity, "profile data updated", Toast.LENGTH_SHORT).show()
                activity.profileUpdateSuccess()


            }.addOnFailureListener { e ->
                activity.hideProgressDialog()
                Log.e(
                    activity.javaClass.simpleName,
                    "Error while creating a board",
                    e
                )
                Toast.makeText(activity, "error while updating data", Toast.LENGTH_SHORT).show()


            }
    }



    fun loadUserData(activity: Activity, readsBoardsList: Boolean = false) {

        // Here we pass the collection name from which we wants the data.
        mFireStore.collection(Constants.USERS)
            // The document id to get the Fields of user.
            .document(getCurrentUserID())
            .get()
            .addOnSuccessListener { document ->
                Log.e(activity.javaClass.simpleName, document.toString())

                // Here we have received the document snapshot which is converted into the User Data model object.
                val loggedInUser = document.toObject(User::class.java)!!


                when (activity) {
                    is SignInActivity -> {
                        activity.signInSuccess(loggedInUser)
                    }
                    is MainActivity -> {
                        activity.updateNavigationUserDetails(loggedInUser, readsBoardsList)
                    }
                    is MyProfileActivity -> {
                        activity.setUserDataInUI(loggedInUser)
                    }
                    // END
                }
                // END
            }
            .addOnFailureListener { e ->

                when (activity) {
                    is SignInActivity -> {
                        activity.hideProgressDialog()
                    }
                    is MainActivity -> {
                        activity.hideProgressDialog()
                    }
                    // END
                }
                // END
                Log.e(
                    activity.javaClass.simpleName,
                    "Error while getting loggedIn user details",
                    e
                )
            }
    }

    /**
     * A function for getting the user id of current logged user.
     */
    fun getCurrentUserID(): String {
        // An Instance of currentUser using FirebaseAuth
        val currentUser = FirebaseAuth.getInstance().currentUser

        // A variable to assign the currentUserId if it is not null or else it will be blank.
        var currentUserID = ""
        if (currentUser != null) {
            currentUserID = currentUser.uid
        }

        return currentUserID

    }

    fun getBoardDetails(activity: TaskListActivity, documentId: String) {
        mFireStore.collection(Constants.BOARDS)
            .document(documentId).get().addOnSuccessListener { document ->
                Log.i(activity.javaClass.simpleName, document.toString())

                //get board details
                val board = document.toObject(Board::class.java)!!
                board.documentId = document.id
                activity.boardDetails(board)


            }.addOnFailureListener { e ->
                activity.hideProgressDialog()
                Log.e(
                    activity.javaClass.simpleName,
                    "Error while getting a board document",
                    e
                )
            }
    }

    fun addUpdateTaskList(activity: TaskListActivity, board: Board) {
        val taskListHaspMap = HashMap<String, Any>()
        taskListHaspMap[Constants.TASK_LIST] = board.taskList

        mFireStore.collection(Constants.BOARDS)
            .document(board.documentId).update(taskListHaspMap)
            .addOnSuccessListener {
                Toast.makeText(activity, "TaskList updated Successfully", Toast.LENGTH_SHORT).show()
                activity.addUpdateTaskListSuccess()
            }
            .addOnFailureListener {
                activity.hideProgressDialog()
                Toast.makeText(activity, "Failed to create task", Toast.LENGTH_SHORT).show()
            }
    }
}