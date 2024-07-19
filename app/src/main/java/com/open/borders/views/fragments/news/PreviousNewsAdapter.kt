package com.open.borders.views.fragments.news

import android.annotation.SuppressLint
import android.content.Intent
import android.net.Uri
import android.util.Log
import android.view.LayoutInflater
import android.view.MotionEvent
import android.view.View
import android.view.ViewGroup
import android.widget.ProgressBar
import androidx.core.content.ContextCompat
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.open.borders.R
import com.open.borders.databinding.PreviousNewsLayoutBinding
import com.open.borders.extensions.setHtmlText
import com.open.borders.extensions.viewGone
import com.open.borders.extensions.viewVisible
import com.open.borders.models.generalModel.newsModel.NewsResponseItem
import com.open.borders.views.activities.baseActivity.BaseActivity
import kotlinx.serialization.InternalSerializationApi
import java.text.DateFormat
import java.text.SimpleDateFormat
import java.util.*
import java.util.regex.Matcher
import java.util.regex.Pattern

@InternalSerializationApi
class PreviousNewsAdapter(
    private var context: BaseActivity,
    private val listener: NewsInterface,
    private val newsList: MutableList<NewsResponseItem?>
) : RecyclerView.Adapter<RecyclerView.ViewHolder>() {
    //    var newsList = mutableListOf<NewsResponseItem>()
    private val VIEW_TYPE_LOADING = 0
    private val VIEW_TYPE_NORMAL = 1
    private var isLoaderVisible = false

    inner class PreviousNewsViewHolder constructor(itemView: View) :
        RecyclerView.ViewHolder(itemView) {
        var binding = PreviousNewsLayoutBinding.bind(itemView)

        var titleTv = binding.titleText
        var description = binding.mDescription
        var date = binding.dateTv
        var imageView = binding.imageView
        var newsLayoutId = binding.newsLayoutId

        var reliv = binding.relIv
        var youtubePlay = binding.youtubePlay
    }

    inner class ProgressHolder constructor(itemView: View) : RecyclerView.ViewHolder(itemView) {
        private val progressBar: ProgressBar

        init {
            progressBar = itemView.findViewById(R.id.progressBar)
        }
    }

    private val mInflater: LayoutInflater by lazy {
        LayoutInflater.from(context)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): RecyclerView.ViewHolder {

        return when (viewType) {
            VIEW_TYPE_NORMAL -> PreviousNewsViewHolder(
                LayoutInflater.from(parent.context)
                    .inflate(R.layout.previous_news_layout, parent, false)
            )
            VIEW_TYPE_LOADING -> ProgressHolder(
                LayoutInflater.from(parent.context).inflate(R.layout.item_loading, parent, false)
            )
            else -> null!!
        }
//        val view = mInflater.inflate(R.layout.previous_news_layout, parent, false)
//        return PreviousNewsViewHolder(view)
    }

    @SuppressLint("ClickableViewAccessibility")
    override fun onBindViewHolder(mHolder: RecyclerView.ViewHolder, position: Int) {
        if (getItemViewType(position) == VIEW_TYPE_NORMAL) {
            val holder: PreviousNewsViewHolder = mHolder as PreviousNewsViewHolder
            val items = newsList[position]

            items?.title?.rendered?.let {
                holder.titleTv.setHtmlText(it)
            }

//        holder.description?.setHtml(
//            Html.fromHtml(items.content?.rendered.toString()).toString(),
//            HtmlResImageGetter(context),context
//        )

            items?.excerpt?.rendered?.let { holder.description?.setHtmlText(it) }
//        holder.date.text = items.date?.toDate()?.formatTo("MMMM dd, yyyy")
            try {
                val outputFormat: DateFormat = SimpleDateFormat("MMMM dd, yyyy", Locale.US)
                val inputFormat: DateFormat = SimpleDateFormat("yyyy-MM-dd'T'hh:mm:ss", Locale.US)
                val date = items?.date?.let { inputFormat.parse(it) }
                val outputText = date?.let { outputFormat.format(it) }
                Log.e("Date", items?.date.toString())
                if (outputText != null) {
                    Log.e("FormattedDate", outputText)
                }
                holder.date.text = outputText
            } catch (e: Exception) {
                e.printStackTrace()
            }

//        holder.date.text = android.text.format.DateFormat.format("MMMM dd, yyyy", items.date?.toDate()).toString()

            val image = items?.x_featured_media_original

            if (items?.x_metadata?.youtubeVideo != null && items?.x_metadata?.youtubeVideo != "") {
                holder.youtubePlay?.viewVisible()
                var thumnail = "https://img.youtube.com/vi/" + getYouTubeId(items?.x_metadata?.youtubeVideo!!) + "/0.jpg"
//                var thumnail=items?.x_metadata?.youtubeVideo+"/0.jpg"

                Glide.with(context)
                    .load(thumnail)
                    .placeholder(R.drawable.dummy5)
                    .into(holder.imageView)

            } else {

                holder.youtubePlay?.viewGone()
                Glide.with(context)
                    .load(image)
                    .placeholder(R.drawable.dummy5)
                    .into(holder.imageView)
            }


            holder.itemView.setOnTouchListener { _, motionEvent ->
                when (motionEvent.action) {
                    MotionEvent.ACTION_DOWN -> {
                        holder.newsLayoutId.background =
                            ContextCompat.getDrawable(context, R.drawable.actionlink_bg)
                        holder.titleTv.setTextColor(ContextCompat.getColor(context, R.color.white))
                        holder.date.setTextColor(ContextCompat.getColor(context, R.color.white))
                        holder.description?.setTextColor(
                            ContextCompat.getColor(
                                context,
                                R.color.white
                            )
                        )
                    }
                    MotionEvent.ACTION_UP -> {
                        holder.newsLayoutId.background =
                            ContextCompat.getDrawable(context, R.drawable.drawable_bg_with_border)
                        holder.titleTv.setTextColor(
                            ContextCompat.getColor(
                                context,
                                R.color.brown_new
                            )
                        )
                        holder.date.setTextColor(ContextCompat.getColor(context, R.color.brown_new))
                        holder.description?.setTextColor(
                            ContextCompat.getColor(
                                context,
                                R.color.brown_new
                            )
                        )

                        listener.onNewsItemClick(position, items!!)
                    }
                    MotionEvent.ACTION_CANCEL -> {
                        holder.newsLayoutId.background =
                            ContextCompat.getDrawable(context, R.drawable.drawable_bg_with_border)
                        holder.titleTv.setTextColor(
                            ContextCompat.getColor(
                                context,
                                R.color.brown_new
                            )
                        )
                        holder.date.setTextColor(ContextCompat.getColor(context, R.color.brown_new))
                        holder.description?.setTextColor(
                            ContextCompat.getColor(
                                context,
                                R.color.brown_new
                            )
                        )
                    }
                }
                true
            }


//        holder.itemView.setOnClickListener {
//            listener.onNewsItemClick(position, items)
//        }

            holder.imageView.setOnClickListener {
                if (items?.x_metadata?.youtubeVideo != null && items?.x_metadata?.youtubeVideo != "") {
                    val intent = Intent(Intent.ACTION_VIEW)
                    intent.data = Uri.parse(items?.x_metadata?.youtubeVideo)
                    intent.setPackage("com.google.android.youtube")
                    context.startActivity(intent)
                } else {
                    if (image != null) {
                        listener.onImageClick(position, image)
                    }
                }
            }
        }
    }

    private fun getYouTubeId(youTubeUrl: String): String? {
        val pattern = "(?<=youtu.be/|watch\\?v=|/videos/|embed\\/)[^#\\&\\?]*"
        val compiledPattern: Pattern = Pattern.compile(pattern)
        val matcher: Matcher = compiledPattern.matcher(youTubeUrl)
        return if (matcher.find()) {
            matcher.group()
        } else {
            "error"
        }
    }

    override fun getItemCount(): Int {
        return newsList.size
    }

    override fun getItemViewType(position: Int): Int {
        return if (isLoaderVisible) {
            if (position == newsList.size - 1) VIEW_TYPE_LOADING else VIEW_TYPE_NORMAL
        } else {
            VIEW_TYPE_NORMAL
        }
    }


    //it is use for add null into list so that we can show loader in 11th position of of list

    fun addLoading() {
        isLoaderVisible = true
        newsList.add(null)
        notifyItemInserted(newsList.size - 1)
    }

    fun addItems(postItems: ArrayList<NewsResponseItem>) {
        newsList.addAll(postItems)
        notifyDataSetChanged()
    }

    fun clearItems() {
        newsList.clear()
        notifyDataSetChanged()
    }


    //Basically its remove null from list
    fun removeLoading() {
        isLoaderVisible = false
        val position: Int = newsList.size - 1
        val item: NewsResponseItem? = newsList[position]
        if (item == null) {
            newsList.removeAt(position)
            notifyItemRemoved(position)
        }
    }

//    private fun getItem(position: Int): NewsResponseItem {
//        return newsList[position]
//    }

    interface NewsInterface {
        fun onNewsItemClick(index: Int, data: NewsResponseItem)
        fun onImageClick(index: Int, imageUrl: String)
    }
}