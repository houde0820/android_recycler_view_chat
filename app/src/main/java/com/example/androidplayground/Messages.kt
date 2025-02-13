package com.example.androidplayground

object Messages {
    val messages = listOf(
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
    ).mapIndexed() { index, text ->
        Item(index.toLong(), text)
    }
}