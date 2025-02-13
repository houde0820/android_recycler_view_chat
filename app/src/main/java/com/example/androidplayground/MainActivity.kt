package com.example.androidplayground

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.activity.ComponentActivity
import androidx.activity.enableEdgeToEdge
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

private const val TAG = "MainActivity"

class MainActivity : ComponentActivity() {
    private val binding by lazy { ActivityMainBinding.inflate(layoutInflater) }
    private val adapter by lazy {
        ItemAdapter()
    }
    private var id = 1L
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
        binding.recyclerView.itemAnimator = null
        binding.generate.setOnClickListener {
            if (job?.isActive == true) {
                return@setOnClickListener
            }
            var item = Item.MessageItem(id++, "")
            items[item.id] = item
            updateItems()
            job = lifecycleScope.launch {
                MessageApi.generate()
                    .collect { text ->
                        item = item.copy(text = item.text + text, isGenerating = true).also {
                            items[it.id] = it
                            updateItems()
                        }
                        // scroll to bottom here
                        val isAtBottom =
                            layoutManager.findLastCompletelyVisibleItemPosition() == adapter.itemCount - 1
                        if (isAtBottom) {
                            layoutManager.scrollToPosition(adapter.itemCount - 1)
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
        adapter.updateItems(items.values.toMutableList()
            .apply {
                add(0, Item.FirstItem)
                add(Item.LastItem)
            }
        )
    }


}

sealed class Item {

    abstract val id: Long


    data class MessageItem(
        override val id: Long,
        val text: String,
        val isGenerating: Boolean = false
    ) : Item()

    data object LastItem : Item() {
        override val id = Long.MAX_VALUE
    }

    data object FirstItem : Item() {
        override val id = Long.MIN_VALUE
    }
}

class ItemAdapter : RecyclerView.Adapter<ItemAdapter.ItemViewHolder>() {

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

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ItemViewHolder {
        return when (viewType) {
            ItemViewHolder.ITEM_TYPE_MESSAGE -> {
                ItemViewHolder.MessageViewHolder(
                    ItemViewBinding.inflate(
                        LayoutInflater.from(parent.context),
                        parent,
                        false
                    )
                )
            }

            ItemViewHolder.ITEM_TYPE_FIRST -> {
                ItemViewHolder.FirstViewHolder(View(
                    parent.context
                ).apply
                {
                    layoutParams = ViewGroup.LayoutParams(1, 1)
                })
            }

            ItemViewHolder.ITEM_TYPE_LAST -> {
                ItemViewHolder.LastViewHolder(View(
                    parent.context
                ).apply
                {
                    layoutParams = ViewGroup.LayoutParams(1, 1)
                })
            }

            else -> throw IllegalArgumentException("Unknown viewType: $viewType")
        }
    }

    override fun onBindViewHolder(holder: ItemViewHolder, position: Int) {
        when (val item = items[position]) {
            is Item.MessageItem -> (holder as ItemViewHolder.MessageViewHolder).bindData(item)
            is Item.FirstItem -> {
            }

            is Item.LastItem -> {
            }
        }
    }

    override fun getItemId(position: Int): Long {
        return items[position].id
    }

    override fun getItemCount(): Int {
        return items.size
    }

    override fun getItemViewType(position: Int): Int {
        return when (items[position]) {
            is Item.MessageItem -> ItemViewHolder.ITEM_TYPE_MESSAGE
            is Item.FirstItem -> ItemViewHolder.ITEM_TYPE_FIRST
            is Item.LastItem -> ItemViewHolder.ITEM_TYPE_LAST
        }
    }

    sealed class ItemViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        class MessageViewHolder(private val binding: ItemViewBinding) :
            ItemViewHolder(binding.root) {
            fun bindData(data: Item) {
                when (data) {
                    is Item.MessageItem -> {
                        binding.text.text = data.text
                    }

                    is Item.FirstItem -> {
                        binding.root.visibility = View.GONE
                    }

                    is Item.LastItem -> {
                        binding.root.visibility = View.GONE
                    }
                }
            }
        }

        class FirstViewHolder(itemView: View) : ItemViewHolder(itemView)
        class LastViewHolder(itemView: View) : ItemViewHolder(itemView)

        companion object {
            const val ITEM_TYPE_MESSAGE = 0
            const val ITEM_TYPE_FIRST = 1
            const val ITEM_TYPE_LAST = 2
        }
    }
}

object MessageApi {
    private val messages = listOf(
        "how to make a recyclerview stick to bottom if recyclerview is at bottom, and when last item or its content and size changed",
        "To make a RecyclerView stick to the bottom when it’s already scrolled to the bottom, and also handle dynamic content changes, follow these steps:\n" +
                "\n" +
                "1. Detect Scroll Position\n" +
                "\n" +
                "Check if the RecyclerView is at the bottom before updating its content.\n" +
                "\n" +
                "private boolean isAtBottom(RecyclerView recyclerView) {\n" +
                "    LinearLayoutManager layoutManager = (LinearLayoutManager) recyclerView.getLayoutManager();\n" +
                "    if (layoutManager != null) {\n" +
                "        int lastVisibleItemPosition = layoutManager.findLastCompletelyVisibleItemPosition();\n" +
                "        return lastVisibleItemPosition == layoutManager.getItemCount() - 1;\n" +
                "    }\n" +
                "    return false;\n" +
                "}\n" +
                "\n" +
                "2. Scroll to Bottom When Needed\n" +
                "\n" +
                "After updating the data, if it was already at the bottom, scroll it down:\n" +
                "\n" +
                "private void scrollToBottom(RecyclerView recyclerView) {\n" +
                "    recyclerView.post(() -> recyclerView.scrollToPosition(recyclerView.getAdapter().getItemCount() - 1));\n" +
                "}\n" +
                "\n" +
                "3. Listen for Data Changes\n" +
                "\n" +
                "Override onChanged() in RecyclerView.Adapter’s registerAdapterDataObserver:\n" +
                "\n" +
                "recyclerView.getAdapter().registerAdapterDataObserver(new RecyclerView.AdapterDataObserver() {\n" +
                "    @Override\n" +
                "    public void onChanged() {\n" +
                "        if (isAtBottom(recyclerView)) {\n" +
                "            scrollToBottom(recyclerView);\n" +
                "        }\n" +
                "    }\n" +
                "\n" +
                "    @Override\n" +
                "    public void onItemRangeInserted(int positionStart, int itemCount) {\n" +
                "        if (isAtBottom(recyclerView)) {\n" +
                "            scrollToBottom(recyclerView);\n" +
                "        }\n" +
                "    }\n" +
                "});\n" +
                "\n" +
                "This ensures the RecyclerView stays at the bottom when new items are added or their size changes.",
        "can you get recycleerview's max scroll offset",
        "Yes, you can get the RecyclerView’s maximum scroll offset by checking its computeVerticalScrollRange and computeVerticalScrollOffset.\n" +
                "\n" +
                "Get Maximum Scroll Offset\n" +
                "\n" +
                "The maximum scroll offset is calculated as:\n" +
                "\n" +
                "￼\n" +
                "\n" +
                "Here’s how you can get it:\n" +
                "\n" +
                "private int getMaxScrollOffset(RecyclerView recyclerView) {\n" +
                "    return recyclerView.computeVerticalScrollRange() - recyclerView.getHeight();\n" +
                "}\n" +
                "\n" +
                "Get Current Scroll Offset\n" +
                "\n" +
                "To get the current scroll offset:\n" +
                "\n" +
                "private int getCurrentScrollOffset(RecyclerView recyclerView) {\n" +
                "    return recyclerView.computeVerticalScrollOffset();\n" +
                "}\n" +
                "\n" +
                "Check if RecyclerView is at Bottom\n" +
                "\n" +
                "Now you can compare:\n" +
                "\n" +
                "private boolean isAtBottom(RecyclerView recyclerView) {\n" +
                "    return getCurrentScrollOffset(recyclerView) >= getMaxScrollOffset(recyclerView);\n" +
                "}\n" +
                "\n" +
                "This method works with LinearLayoutManager, GridLayoutManager, and StaggeredGridLayoutManager."
    )

    private var index = 0

    fun generate(): Flow<String> {
        val item = messages[index]
        index = (index + 1) % messages.size
        return flow {
            item.toCharArray()
                .forEach {
                    emit(it.toString())
                    kotlinx.coroutines.delay(10)
                }
        }
    }
}