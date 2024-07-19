package com.open.borders.models.responseModel

data class TermsAndConditionResponse(
    val _links: Links,
    val acf: List<Any>,
    val author: Int,
    val comment_status: String,
    val content: Content,
    val date: String,
    val date_gmt: String,
    val excerpt: Excerpt,
    val featured_media: Int,
    val guid: Guid,
    val id: Int,
    val link: String,
    val menu_order: Int,
    val meta: List<Any>,
    val modified: String,
    val modified_gmt: String,
    val parent: Int,
    val ping_status: String,
    val slug: String,
    val status: String,
    val template: String,
    val title: Title,
    val type: String,
    val x_author: String,
    val x_date: String,
    val x_gravatar: String
)

data class Links(
    val about: List<About>,
    val author: List<Author>,
    val collection: List<Collection>,
    val curies: List<Cury>,
    val predecessorversion: List<PredecessorVersion>,
    val replies: List<Reply>,
    val self: List<Self>,
    val versionhistory: List<VersionHistory>,
    val wpattachment: List<WpAttachment>
)

data class Content(
    val rendered: String
)

data class Excerpt(
    val `protected`: Boolean,
    val rendered: String
)

data class Guid(
    val rendered: String
)

data class Title(
    val rendered: String
)

data class About(
    val href: String
)

data class Author(
    val embeddable: Boolean,
    val href: String
)

data class Collection(
    val href: String
)

data class Cury(
    val href: String,
    val name: String,
    val templated: Boolean
)

data class PredecessorVersion(
    val href: String,
    val id: Int
)

data class Reply(
    val embeddable: Boolean,
    val href: String
)

data class Self(
    val href: String
)

data class VersionHistory(
    val count: Int,
    val href: String
)

data class WpAttachment(
    val href: String
)