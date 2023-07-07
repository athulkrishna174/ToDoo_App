package com.example.todoo.fragments

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.fragment.app.DialogFragment
import com.example.todoo.databinding.FragmentAddToDooPopUpBinding
import com.example.todoo.utils.model.ToDooData
import com.google.android.material.textfield.TextInputEditText


class AddToDooPopUpFragment : DialogFragment() {

    private lateinit var binding: FragmentAddToDooPopUpBinding
    private lateinit var listener: DialogNextButtonListener
    private var toDooData: ToDooData? = null

    fun setListener(listener: DialogNextButtonListener){
        this.listener = listener
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        // Inflate the layout for this fragment
        binding = FragmentAddToDooPopUpBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        if (arguments != null){
            toDooData = ToDooData(arguments?.getString("taskId").toString(), arguments?.getString("task").toString())

            binding.todoEt.setText(toDooData?.task)
        }
        popUpEvents()
    }

    private fun popUpEvents() {
        binding.todoNextButton.setOnClickListener{
            val todoText = binding.todoEt.text.toString()

            if (todoText.isNotEmpty()){
                if (toDooData == null){
                    listener.onSaveTask(todoText, binding.todoEt)
                }else{
                    toDooData?.task = todoText
                    listener.onUpdateTask(toDooData!!, binding.todoEt)
                }

            }else{
                Toast.makeText(context, "Enter a ToDoo", Toast.LENGTH_SHORT).show()
            }
        }

        binding.todoClose.setOnClickListener{
            dismiss()
        }
    }

    interface DialogNextButtonListener{
        fun onSaveTask(todoText : String, todoEt : TextInputEditText)
        fun onUpdateTask(toDooData: ToDooData, todoEt : TextInputEditText)
    }

    companion object {
        const val TAG:String = "AddToDooPopUpFragment"

        @JvmStatic
        fun newInstance(taskId: String, task: String):AddToDooPopUpFragment = AddToDooPopUpFragment().apply {
            arguments = Bundle().apply {
                putString("taskId", taskId)
                putString("task", task)
            }
        }
    }
}