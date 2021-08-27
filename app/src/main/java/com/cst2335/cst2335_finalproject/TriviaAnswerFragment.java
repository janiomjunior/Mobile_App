package com.cst2335.cst2335_finalproject;

import android.content.Context;
import android.graphics.Color;
import android.os.Bundle;

import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.Fragment;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;

import com.cst2335.cst2335_finalproject.trivia.TriviaPlayActivity;

/**
 * A simple {@link Fragment} subclass.
 * Use the {@link TriviaAnswerFragment # newInstance} factory method to
 * create an instance of this fragment.
 */
public class TriviaAnswerFragment extends Fragment {

    private Bundle dataFromActivity;
//    private long id;
//    private AppCompatActivity parentActivity;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {

        dataFromActivity = getArguments();
//        id = dataFromActivity.getLong(ChatRoomActivity.ITEM_ID);

        // Inflate the layout for this fragment
        View result =  inflater.inflate(R.layout.trivia_multiple_fragment1, container, false);

        Button answer = result.findViewById(R.id.correctAnswer);
        answer.setText(dataFromActivity.getString(TriviaPlayActivity.CORRECT_ANSWER));


        Button answer2 = result.findViewById(R.id.incorrectAnswer1);
        answer2.setText(dataFromActivity.getString(TriviaPlayActivity.INCORRECT_ANSWER1));


        Button answer3 = result.findViewById(R.id.incorrectAnswer2);
        answer3.setText(dataFromActivity.getString(TriviaPlayActivity.INCORRECT_ANSWER2));


        Button answer4 = result.findViewById(R.id.incorrectAnswer3);
        answer4.setText(dataFromActivity.getString(TriviaPlayActivity.INCORRECT_ANSWER3));


        return result;
    }

//    @Override
//    public void onAttach(Context context) {
//        super.onAttach(context);
//
//        //context will either be FragmentExample for a tablet, or EmptyActivity for phone
//        parentActivity = (AppCompatActivity)context;
//    }

}