package com.open.borders.models.generalModel.newsModel

import android.os.Parcelable
import com.google.gson.annotations.SerializedName
import kotlinx.serialization.Serializable
import kotlinx.serialization.SerialName

class NewsResponse : ArrayList<NewsResponseItem>()

@Serializable
data class NewsResponseItem(
    @SerialName("acf")
    val acf: List<String>? = null,
    @SerialName("author")
    val author: Int? = 0,
    @SerialName("categories")
    val categories: List<Int>? = listOf(),
    @SerialName("comment_status")
    val commentStatus: String? = "",
    @SerialName("content")
    val content: Content? = Content(),
    @SerialName("date")
    val date: String? = "",
    @SerialName("date_gmt")
    val dateGmt: String? = "",
    @SerialName("_embedded")
    val _embedded: Embedded? = Embedded(),
    @SerialName("excerpt")
    val excerpt: Excerpt? = Excerpt(),
    @SerialName("featured_media")
    val featuredMedia: Int? = 0,
    @SerialName("format")
    val format: String? = "",
    @SerialName("guid")
    val guid: Guid? = Guid(),
    @SerialName("id")
    val id: Int? = 0,
    @SerialName("link")
    val link: String? = "",
    @SerialName("_links")
    val links: Links? = Links(),

    @SerialName("meta")
//    val meta: List<String>? = null
    val meta: Meta? = Meta(),
    @SerialName("modified")
    val modified: String? = "",
    @SerialName("modified_gmt")
    val modifiedGmt: String? = "",
    @SerialName("ping_status")
    val pingStatus: String? = "",
    @SerialName("slug")
    val slug: String? = "",
    @SerialName("status")
    val status: String? = "",
    @SerialName("sticky")
    val sticky: Boolean? = false,
    @SerialName("tags")
    val tags: List<String>? = null,
    @SerialName("template")
    val template: String? = "",
    @SerialName("title")
    val title: Title? = Title(),
    @SerialName("type")
    val type: String? = "",
    @SerialName("x_featured_media_original")
    val x_featured_media_original: String? = null,
    @SerialName("x_metadata")
    val x_metadata: Xmetadata = Xmetadata()

)

@Serializable
data class Xmetadata(
    @SerializedName("classic_editor_remember")
    var classicEditorRemember: String? = null,
    @SerializedName("_last_editor_used_jetpack")
    var LastEditorUsedJetpack: String? = null,
    @SerializedName("om_disable_all_campaigns")
    var omDisableAllCampaigns: String? = null,
    @SerializedName("_aioseo_title")
    var AioseoTitle: String? = null,
    @SerializedName("_aioseo_description")
    var AioseoDescription: String? = null,
    @SerializedName("_aioseo_keywords")
    var AioseoKeywords: String? = null,
    @SerializedName("_aioseo_og_title")
    var AioseoOgTitle: String? = null,
    @SerializedName("_aioseo_og_description")
    var AioseoOgDescription: String? = null,
    @SerializedName("_aioseo_og_article_section")
    var AioseoOgArticleSection: String? = null,
    @SerializedName("_aioseo_og_article_tags")
    var AioseoOgArticleTags: String? = null,
    @SerializedName("_aioseo_twitter_title")
    var AioseoTwitterTitle: String? = null,
    @SerializedName("_aioseo_twitter_description")
    var AioseoTwitterDescription: String? = null,
    @SerializedName("inline_featured_image")
    var inlineFeaturedImage: String? = null,
    @SerializedName("_wpas_skip_2425583")
    var WpasSkip2425583: String? = null,
    @SerializedName("_publicize_twitter_user")
    var PublicizeTwitterUser: String? = null,

    @SerializedName("youtube_video")
    var youtubeVideo: String? = null,
    @SerializedName("_youtube_video")
    var _youtubeVideo: String? = null,

    @SerializedName("_wpas_done_all")
    var WpasDoneAll: String? = null,
    @SerializedName("_mi_skip_tracking")
    var MiSkipTracking: String? = null,
    @SerializedName("_wpas_skip_28470814")
    var WpasSkip28470814: String? = null,
    @SerializedName("_wpas_skip_2773464")
    var WpasSkip2773464: String? = null,
    @SerializedName("_wpas_skip_28470817")
    var WpasSkip28470817: String? = null,
    @SerializedName("_wpas_skip_28470821")
    var WpasSkip28470821: String? = null,
    @SerializedName("rs_page_bg_color")
    var rsPageBgColor: String? = null
)


@Serializable
data class Content(
    @SerialName("protected")
    val `protected`: Boolean? = false,
    @SerialName("rendered")
    val rendered: String? = ""
)

@Serializable
data class Meta(
    @SerialName("inline_featured_image")
    val inline_featured_image: Boolean = false
)

@Serializable
data class Embedded(
    @SerialName("author")
    val author: List<Author>? = listOf(),
    @SerialName("wp:featuredmedia")
    val wpFeaturedmedia: List<WpFeaturedmedia>? = listOf(),
    @SerialName("wp:term")
    val wpTerm: List<List<WpTerm>>? = listOf()
)

@Serializable
data class Excerpt(
    @SerialName("protected")
    val protected: Boolean? = false,
    @SerialName("rendered")
    val rendered: String? = ""
)

@Serializable
data class Guid(
    @SerialName("rendered")
    val rendered: String? = ""
)

@Serializable
data class Links(
    @SerialName("about")
    val about: List<About>? = listOf(),
    @SerialName("author")
    val author: List<Author>? = listOf(),
    @SerialName("collection")
    val collection: List<Collection>? = listOf(),
    @SerialName("curies")
    val curies: List<Cury>? = listOf(),
    @SerialName("replies")
    val replies: List<Reply>? = listOf(),
    @SerialName("self")
    val self: List<Self>? = listOf(),
    @SerialName("version-history")
    val versionHistory: List<VersionHistory>? = listOf(),
    @SerialName("wp:attachment")
    val wpAttachment: List<WpAttachment>? = listOf(),
    @SerialName("wp:featuredmedia")
    val wpFeaturedmedia: List<WpFeaturedmedia>? = listOf(),
    @SerialName("wp:term")
    val wpTerm: List<WpTerm>? = listOf()
)


@Serializable
data class Author(
    @SerialName("avatar_urls")
    val avatarUrls: AvatarUrls? = AvatarUrls(),
    @SerialName("description")
    val description: String? = "",
    @SerialName("id")
    val id: Int? = 0,
    @SerialName("is_super_admin")
    val isSuperAdmin: Boolean? = false,
    @SerialName("link")
    val link: String? = "",
    @SerialName("_links")
    val links: Links? = Links(),
    @SerialName("name")
    val name: String? = "",
    @SerialName("slug")
    val slug: String? = "",
    @SerialName("url")
    val url: String? = "",
    @SerialName("woocommerce_meta")
    val woocommerceMeta: WoocommerceMeta? = WoocommerceMeta()
)

@Serializable
data class WpFeaturedmedia(
    @SerialName("alt_text")
    val altText: String? = "",
    @SerialName("author")
    val author: Int? = 0,
    @SerialName("caption")
    val caption: Caption? = Caption(),
    @SerialName("date")
    val date: String? = "",
    @SerialName("id")
    val id: Int? = 0,
    @SerialName("link")
    val link: String? = "",
    @SerialName("_links")
    val links: Links? = Links(),
    @SerialName("media_details")
    val mediaDetails: MediaDetails? = MediaDetails(),
    @SerialName("media_type")
    val mediaType: String? = "",
    @SerialName("mime_type")
    val mimeType: String? = "",
    @SerialName("slug")
    val slug: String? = "",
    @SerialName("source_url")
    val sourceUrl: String? = "",
    @SerialName("title")
    val title: Title? = Title(),
    @SerialName("type")
    val type: String? = ""
)

@Serializable
data class WpTerm(
    @SerialName("id")
    val id: Int? = 0,
    @SerialName("link")
    val link: String? = "",
    @SerialName("_links")
    val links: Links? = Links(),
    @SerialName("name")
    val name: String? = "",
    @SerialName("slug")
    val slug: String? = "",
    @SerialName("taxonomy")
    val taxonomy: String? = ""
)

@Serializable
data class AvatarUrls(
    @SerialName("24")
    val x24: String? = "",
    @SerialName("48")
    val x48: String? = "",
    @SerialName("96")
    val x96: String? = ""
)

@Serializable
data class WoocommerceMeta(
    @SerialName("activity_panel_inbox_last_read")
    val activityPanelInboxLastRead: String? = "",
    @SerialName("activity_panel_reviews_last_read")
    val activityPanelReviewsLastRead: String? = "",
    @SerialName("android_app_banner_dismissed")
    val androidAppBannerDismissed: String? = "",
    @SerialName("categories_report_columns")
    val categoriesReportColumns: String? = "",
    @SerialName("coupons_report_columns")
    val couponsReportColumns: String? = "",
    @SerialName("customers_report_columns")
    val customersReportColumns: String? = "",
    @SerialName("dashboard_chart_interval")
    val dashboardChartInterval: String? = "",
    @SerialName("dashboard_chart_type")
    val dashboardChartType: String? = "",
    @SerialName("dashboard_leaderboard_rows")
    val dashboardLeaderboardRows: String? = "",
    @SerialName("dashboard_sections")
    val dashboardSections: String? = "",
    @SerialName("help_panel_highlight_shown")
    val helpPanelHighlightShown: String? = "",
    @SerialName("homepage_layout")
    val homepageLayout: String? = "",
    @SerialName("homepage_stats")
    val homepageStats: String? = "",
    @SerialName("orders_report_columns")
    val ordersReportColumns: String? = "",
    @SerialName("products_report_columns")
    val productsReportColumns: String? = "",
    @SerialName("revenue_report_columns")
    val revenueReportColumns: String? = "",
    @SerialName("task_list_tracked_started_tasks")
    val taskListTrackedStartedTasks: String? = "",
    @SerialName("taxes_report_columns")
    val taxesReportColumns: String? = "",
    @SerialName("variations_report_columns")
    val variationsReportColumns: String? = ""
)

@Serializable
data class Collection(
    @SerialName("href")
    val href: String? = ""
)

@Serializable
data class Self(
    @SerialName("href")
    val href: String? = ""
)

@Serializable
data class Caption(
    @SerialName("rendered")
    val rendered: String? = ""
)

@Serializable
data class MediaDetails(
    @SerialName("file")
    val `file`: String? = "",
    @SerialName("height")
    val height: Int? = 0,
    @SerialName("image_meta")
    val imageMeta: ImageMeta? = ImageMeta(),
    @SerialName("sizes")
    val sizes: Sizes? = Sizes(),
    @SerialName("width")
    val width: Int? = 0
)

@Serializable
data class Title(
    @SerialName("rendered")
    val rendered: String? = ""
)

@Serializable
data class About(
    @SerialName("href")
    val href: String? = ""
)

@Serializable
data class Reply(
    @SerialName("embeddable")
    val embeddable: Boolean? = false,
    @SerialName("href")
    val href: String? = ""
)


@Serializable
data class ImageMeta(
    @SerialName("aperture")
    val aperture: String? = "",
    @SerialName("camera")
    val camera: String? = "",
    @SerialName("caption")
    val caption: String? = "",
    @SerialName("copyright")
    val copyright: String? = "",
    @SerialName("created_timestamp")
    val createdTimestamp: String? = "",
    @SerialName("credit")
    val credit: String? = "",
    @SerialName("focal_length")
    val focalLength: String? = "",
    @SerialName("iso")
    val iso: String? = "",
    @SerialName("keywords")
    val keywords: List<String>? = null,
    @SerialName("orientation")
    val orientation: String? = "",
    @SerialName("shutter_speed")
    val shutterSpeed: String? = "",
    @SerialName("title")
    val title: String? = ""
)

@Serializable
class Sizes

@Serializable
data class Cury(
    @SerialName("href")
    val href: String? = "",
    @SerialName("name")
    val name: String? = "",
    @SerialName("templated")
    val templated: Boolean? = false
)


@Serializable
data class WpPostType(
    @SerialName("href")
    val href: String? = ""
)

@Serializable
data class VersionHistory(
    @SerialName("count")
    val count: Int? = 0,
    @SerialName("href")
    val href: String? = ""
)

@Serializable
data class WpAttachment(
    @SerialName("href")
    val href: String? = ""
)
