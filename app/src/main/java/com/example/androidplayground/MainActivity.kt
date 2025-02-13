package com.example.androidplayground

import android.os.Bundle
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.activity.ComponentActivity
import androidx.activity.enableEdgeToEdge
import androidx.core.view.setPadding
import androidx.lifecycle.lifecycleScope
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.androidplayground.databinding.ActivityMainBinding
import com.example.androidplayground.databinding.ItemViewBinding
import kotlinx.coroutines.Job
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flow
import kotlinx.coroutines.launch

class MainActivity : ComponentActivity() {
    private val binding by lazy { ActivityMainBinding.inflate(layoutInflater) }
    private val adapter by lazy {
        ItemAdapter()
    }
    private var id = 0L
    private val items = LinkedHashMap<Long, Item>()
    private var job: Job? = null
    private val layoutManager by lazy {
        binding.recyclerView.layoutManager as LinearLayoutManager
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContentView(binding.root)
        binding.root.fitsSystemWindows = true
        binding.recyclerView.adapter = adapter
        binding.generate.setOnClickListener {
            if (job?.isActive == true) {
                return@setOnClickListener
            }
            var item = Item(id++, "")
            items[item.id] = item
            updateItems()
            job = lifecycleScope.launch {
                generate().collect { text ->
                    item = item.copy(text = item.text + text, isGenerating = true).also {
                        items[it.id] = it
                        updateItems()
                    }
                }
                item = item.copy(isGenerating = false).also {
                    items[it.id] = it
                    updateItems()
                }
            }
        }
    }

    private fun updateItems() {
        adapter.updateItems(items.values.reversed().toList())
    }
    private fun generate(): Flow<String> {
        val item = Messages.messages.random()
        return flow {
            item.text.toCharArray()
                .forEach {
                    emit(it.toString())
                    kotlinx.coroutines.delay(10)
                }
        }
    }


}

data class Item(
    val id: Long,
    val text: String,
    val isGenerating: Boolean = false
)

class ItemAdapter : RecyclerView.Adapter<ItemAdapter.ViewHolder>() {

    private var items: List<Item> = emptyList()

    fun updateItems(newItems: List<Item>) {
        DiffUtil.calculateDiff(object : DiffUtil.Callback() {
            override fun getOldListSize(): Int {
                return items.size
            }

            override fun getNewListSize(): Int {
                return newItems.size
            }

            override fun areItemsTheSame(oldItemPosition: Int, newItemPosition: Int): Boolean {
                return items[oldItemPosition] == newItems[newItemPosition]
            }

            override fun areContentsTheSame(oldItemPosition: Int, newItemPosition: Int): Boolean {
                return items[oldItemPosition] == newItems[newItemPosition]
            }
        }).apply {
            items = newItems
        }.dispatchUpdatesTo(this)

    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val binding = ItemViewBinding.inflate(LayoutInflater.from(parent.context), parent, false)
        return ViewHolder(binding)
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        holder.bindData(items[position])
    }

    override fun getItemId(position: Int): Long {
        return items[position].id
    }

    override fun getItemCount(): Int {
        return items.size
    }

    class ViewHolder(private val binding: ItemViewBinding) : RecyclerView.ViewHolder(binding.root) {

        init {
            setIsRecyclable(true)
        }

        fun bindData(data: Item) {
            binding.text.text = data.text
        }



    }
}