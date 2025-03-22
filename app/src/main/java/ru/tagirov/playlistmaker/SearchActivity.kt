package ru.tagirov.playlistmaker
import android.os.Bundle
import android.text.Editable
import android.text.TextWatcher
import android.view.View
import android.view.inputmethod.InputMethodManager
import android.widget.Button
import android.widget.EditText
import android.widget.ImageButton
import android.widget.LinearLayout
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response
import java.util.Timer
import java.util.TimerTask

class SearchActivity : AppCompatActivity() {

    private var searchText: String = ""
    private lateinit var adapter: TrackAdapter
    private lateinit var historyAdapter: TrackAdapter
    private lateinit var searchHistory: SearchHistory
    private lateinit var clearHistoryButton: Button
    private lateinit var retryButton: Button
    private lateinit var emptyResultPlaceholder: LinearLayout
    private lateinit var errorPlaceholder: LinearLayout
    private var lastFailedQuery: String? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        val layoutRes = getLayoutForTheme()
        super.onCreate(savedInstanceState)
        setContentView(layoutRes)

        val backButton = findViewById<ImageButton>(R.id.backButton)
        backButton.setOnClickListener {
            finish()
        }

        searchHistory = SearchHistory(this)

        val searchInput = findViewById<EditText>(R.id.searchInput)
        val clearButton = findViewById<ImageButton>(R.id.clearButton)
        val historyRecyclerView = findViewById<RecyclerView>(R.id.historyRecyclerView)
        val recyclerView = findViewById<RecyclerView>(R.id.recyclerView)
        clearHistoryButton = findViewById(R.id.clearHistoryButton)
        retryButton = findViewById(R.id.retryButton)
        emptyResultPlaceholder = findViewById(R.id.emptyResultPlaceholder)
        errorPlaceholder = findViewById(R.id.errorPlaceholder)

        adapter = TrackAdapter(emptyList()) { track ->
            searchHistory.addTrack(track)
            updateHistory()
        }
        historyAdapter = TrackAdapter(emptyList()) { track ->
            searchHistory.addTrack(track)
            updateHistory()
        }

        retryButton.setOnClickListener {
            lastFailedQuery?.let { query ->
                performSearch(query)
            }
        }

        recyclerView.layoutManager = LinearLayoutManager(this)
        recyclerView.adapter = adapter

        historyRecyclerView.layoutManager = LinearLayoutManager(this)
        historyRecyclerView.adapter = historyAdapter

        searchInput.addTextChangedListener(object : TextWatcher {
            override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {}

            override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {
                searchText = s.toString()
                if (s.isNullOrEmpty()) {
                    clearButton.visibility = View.INVISIBLE
                    showHistory()
                } else {
                    clearButton.visibility = View.VISIBLE
                    hidePlaceholders()
                }
            }

            private var timer = Timer()
            private val DELAY: Long = 500
            override fun afterTextChanged(s: Editable?) {
                timer.cancel()
                timer = Timer()
                if (s.isNullOrEmpty()) {
                    return
                }
                timer.schedule(
                    object : TimerTask() {
                        override fun run() {
                            performSearch(searchText)
                        }
                    },
                    DELAY
                )
            }
        })

        clearButton.setOnClickListener {
            searchInput.text.clear()
            hideKeyboard(searchInput)
            showHistory()
        }

        clearHistoryButton.setOnClickListener {
            searchHistory.clearHistory()
            updateHistory()
        }

        showHistory()
    }

    private fun performSearch(query: String) {
        RetrofitClient.instance.searchTracks(query).enqueue(object : Callback<iTunesSearchResponse> {
            override fun onResponse(
                call: Call<iTunesSearchResponse>,
                response: Response<iTunesSearchResponse>
            ) {
                if (response.isSuccessful) {
                    val tracks = response.body()?.results ?: emptyList()
                    if (tracks.isNotEmpty()) {
                        adapter.updateData(tracks)
                        showSearchResults()
                    } else {
                        showEmptyResultPlaceholder()
                    }
                } else {
                    showErrorPlaceholder()
                }
            }

            override fun onFailure(call: Call<iTunesSearchResponse>, t: Throwable) {
                t.printStackTrace()
                showErrorPlaceholder()
                lastFailedQuery = searchText
            }
        })
    }

    private fun showHistory() {
        hidePlaceholders()
        findViewById<RecyclerView>(R.id.historyRecyclerView).visibility = View.VISIBLE
        findViewById<RecyclerView>(R.id.recyclerView).visibility = View.GONE
        updateHistory()
    }

    private fun hideHistory(){
        findViewById<RecyclerView>(R.id.historyRecyclerView).visibility = View.GONE
        clearHistoryButton.visibility = View.GONE
    }

    private fun showSearchResults() {
        hidePlaceholders()
        findViewById<RecyclerView>(R.id.historyRecyclerView).visibility = View.GONE
        findViewById<RecyclerView>(R.id.recyclerView).visibility = View.VISIBLE
    }

    private fun updateHistory() {
        val history = searchHistory.getHistory()
        historyAdapter.updateData(history)

        clearHistoryButton.visibility = if (history.isEmpty()) View.GONE else View.VISIBLE
    }

    private fun getLayoutForTheme(): Int {
        val isDarkMode = getSharedPreferences("AppPrefs", MODE_PRIVATE).getBoolean("DARK_MODE", false)
        return if (isDarkMode) R.layout.search_dark else R.layout.activity_search
    }

    private fun hideKeyboard(view: View) {
        val imm = getSystemService(INPUT_METHOD_SERVICE) as InputMethodManager
        imm.hideSoftInputFromWindow(view.windowToken, 0)
    }

    private fun hidePlaceholders() {
        emptyResultPlaceholder.visibility = View.GONE
        errorPlaceholder.visibility = View.GONE
    }

    private fun showEmptyResultPlaceholder() {
        hidePlaceholders()
        hideHistory()
        emptyResultPlaceholder.visibility = View.VISIBLE
    }

    private fun showErrorPlaceholder() {
        hidePlaceholders()
        hideHistory()
        errorPlaceholder.visibility = View.VISIBLE
    }
}