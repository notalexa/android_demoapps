/*
 * Copyright (C) 2022 Not Alexa
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package not.alexa.challenge.math;

import android.service.trust.ChallengeService;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import java.security.SecureRandom;
import java.util.HashSet;
import java.util.Random;
import java.util.Set;

public class MathChallenge extends ChallengeService {
    private static final int[][] PERMUTATION=new int[][] {
            {0,1,2,3},
            {0,1,3,2},
            {0,2,1,3},
            {0,2,3,1},
            {0,3,1,2},
            {0,3,2,1},
            {1,0,2,3},
            {1,0,3,2},
            {1,2,0,3},
            {1,2,3,0},
            {1,3,0,2},
            {1,3,2,0},
            {2,1,0,3},
            {2,1,3,0},
            {2,0,1,3},
            {2,0,3,1},
            {2,3,1,0},
            {2,3,0,1},
            {3,1,2,0},
            {3,1,0,2},
            {3,2,1,0},
            {3,2,0,1},
            {3,0,1,2},
            {3,0,2,1}
    };
    Random random=new SecureRandom();

    @Override
    public void onCreate() {
        super.onCreate();
        setChallengeView(R.layout.math_challenge_view,R.id.challenge);
    }

    private String getChoice(int min,int max,Set<Integer> displayed) {
        while(true) {
            int r=random.nextInt(max-min)+min;
            if(!displayed.contains(r)) {
                displayed.add(r);
                return Integer.toString(r);
            }
        }
    }

    @Override
    protected void onChallengeShow(View challenge,View.OnClickListener challengeResolvedCallback) {
        super.onChallengeShow(challenge);
        int a1=random.nextInt(10);
        int a2=random.nextInt(10);
        TextView title=challenge.findViewById(R.id.challenge);
        title.setText(a1+"+"+a2);
        int sum=a1+a2;
        int min=sum-5;
        int max=sum+5;
        if(max>20) {
            min-=(max-20);
            max=20;
        } else if(min<0) {
            max-=min;
            min=0;
        }
        Set<Integer> displayed=new HashSet<>();
        displayed.add(sum);
        Result[] results={
                new Result(Integer.toString(sum),challengeResolvedCallback),
                new Result(getChoice(min,max,displayed),null),
                new Result(getChoice(min,max,displayed),null),
                new Result(getChoice(min,max,displayed),null)
        };
        int[] perm=PERMUTATION[random.nextInt(PERMUTATION.length)];
        results[perm[0]].assign((TextView)challenge.findViewById(R.id.answer1),(ImageView)challenge.findViewById(R.id.result1));
        results[perm[1]].assign((TextView)challenge.findViewById(R.id.answer2),(ImageView)challenge.findViewById(R.id.result2));
        results[perm[2]].assign((TextView)challenge.findViewById(R.id.answer3),(ImageView)challenge.findViewById(R.id.result3));
        results[perm[3]].assign((TextView)challenge.findViewById(R.id.answer4),(ImageView)challenge.findViewById(R.id.result4));
    }

    public class Result implements View.OnClickListener {
        private View.OnClickListener resolvedCallback;
        private String text;
        private int icon;
        private ImageView iconView;
        private TextView answerView;
        private Result(String text,View.OnClickListener resolvedCallback) {
            this.text=text;
            this.resolvedCallback=resolvedCallback;
            this.icon=resolvedCallback!=null?R.drawable.ic_1f44f:R.drawable.ic_1f44e;
        }

        @Override
        public void onClick(final View v) {
            iconView.setBackground(getApplicationContext().getDrawable(icon));
            ((View)iconView.getParent()).setVisibility(View.VISIBLE);
            if(resolvedCallback!=null) {
                new Thread() {
                    public void run() {
                        try {
                            Thread.sleep(500);
                        } catch(Throwable t) {
                        }
                        resolvedCallback.onClick(v);
                    }
                }.start();
            }
        }

        private void assign(TextView answerView,ImageView iconView) {
            this.answerView=answerView;
            this.iconView=iconView;
            answerView.setText(text);
            answerView.setOnClickListener(this);
        }
    }
}
