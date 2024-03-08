package com.alibatuhanak.quizzapp.model

import com.google.firebase.Timestamp
import java.io.Serializable


sealed class Models {
    data class CategoryModel(
        val categoryID:String,
        val categoryName: String, val categoryIcon:String,
        val questions: List<HashMap<String, String>>
    )

    data class SportsQuestion(
        val category_icon: String,
        val category_name: String,
        val questions: List<Question>
    )
    data class Main(
        val a: String,
        val b: List<Den>,

        )
    data class Den(
        val c: String,
        val d: String,

    )


    data class Question(
        val categoryName: String,
        val question: String,
        val correctOption: String,
        val optionA: String,
        val optionB: String,
        val optionC: String,
        val optionD:String

    )

    data class Options(
        val optionC: String,
        val optionD: String,
        val optionA: String,
        val optionB: String
    )

    data class UserModel(var name: String,var username:String, var email: String, var point: Number,val score: HashMap<String,Number>, var rank: Number,var avatarIcon: Number, var timestamp: Timestamp)

    data class RankModel(var username: String, var avatarIcon: Number,var point: Number)


    data class QuestionModel(val question: String,val correctOption:String,val options: HashMap<String,String>)
    data class QuestionModel2(   val correctOption: String?,
                                 val options: HashMap<String,String>,
                                 val question: String?)


    companion object {
        data class UserModel(var name: String,var username:String, var email: String, var point: Number, var rank: Number,var documentID: String,var avatarIcon: Number)

    }
     object UserProfile{
         data class UserModel(var name:String,var username:String, var email: String)

     }
    object HomeProfile{
        data class UserModel(var name: String,var username:String, var email: String, var point: Number, var rank: Number,var documentID: String)
    }


}
