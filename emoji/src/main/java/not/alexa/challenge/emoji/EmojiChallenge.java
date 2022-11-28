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
package not.alexa.challenge.emoji;

import android.service.trust.ChallengeService;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import java.security.SecureRandom;
import java.util.HashSet;
import java.util.Random;
import java.util.Set;

public class EmojiChallenge extends ChallengeService {
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
        setChallengeView(R.layout.emoji_challenge_view,R.id.challenge);
    }

    private Emoji getChoice(Set<Integer> displayed) {
        while(true) {
            int r=random.nextInt(Emoji.values().length);
            if(!displayed.contains(r)) {
                displayed.add(r);
                return Emoji.values()[r];
            }
        }
    }

    @Override
    protected void onChallengeShow(View challenge,View.OnClickListener challengeResolvedCallback) {
        super.onChallengeShow(challenge);
        Emoji[] emojis=Emoji.values();
        int a=random.nextInt(emojis.length);
        Emoji question=emojis[a];
        TextView title=challenge.findViewById(R.id.challenge);
        title.setText(getApplicationContext().getText(question.descr));
        Set<Integer> displayed=new HashSet<>();
        displayed.add(a);
        Result[] results={
                new Result(question,challengeResolvedCallback),
                new Result(getChoice(displayed),null),
                new Result(getChoice(displayed),null),
                new Result(getChoice(displayed),null)
        };
        int[] perm=PERMUTATION[random.nextInt(PERMUTATION.length)];
        results[perm[0]].assign((ImageView)challenge.findViewById(R.id.answer1),(ImageView)challenge.findViewById(R.id.result1));
        results[perm[1]].assign((ImageView)challenge.findViewById(R.id.answer2),(ImageView)challenge.findViewById(R.id.result2));
        results[perm[2]].assign((ImageView)challenge.findViewById(R.id.answer3),(ImageView)challenge.findViewById(R.id.result3));
        results[perm[3]].assign((ImageView)challenge.findViewById(R.id.answer4),(ImageView)challenge.findViewById(R.id.result4));
    }

    public class Result implements View.OnClickListener {
        private View.OnClickListener resolvedCallback;
        private Emoji emoji;
        private int icon;
        private ImageView iconView;
        private ImageView answerView;
        private Result(Emoji emoji,View.OnClickListener resolvedCallback) {
            this.emoji=emoji;
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

        private void assign(ImageView answerView,ImageView iconView) {
            this.answerView=answerView;
            this.iconView=iconView;
            answerView.setBackground(getApplication().getDrawable(emoji.id));
            answerView.setOnClickListener(this);
        }
    }

    public enum Emoji {
        Icon1f600(R.drawable.ic_1f600, R.string.ic_1f600,"grinning face"),
        Icon1f603(R.drawable.ic_1f603,R.string.ic_1f603,"grinning face with big eyes"),
        Icon1f604(R.drawable.ic_1f604,R.string.ic_1f604,"grinning face with smiling eyes"),
        Icon1f601(R.drawable.ic_1f601,R.string.ic_1f601,"beaming face with smiling eyes"),
        Icon1f606(R.drawable.ic_1f606,R.string.ic_1f606,"grinning squinting face"),
        Icon1f605(R.drawable.ic_1f605,R.string.ic_1f605,"grinning face with sweat"),
        Icon1f923(R.drawable.ic_1f923,R.string.ic_1f923,"rolling on the floor laughing"),
        Icon1f602(R.drawable.ic_1f602,R.string.ic_1f602,"face with tears of joy"),
        Icon1f642(R.drawable.ic_1f642,R.string.ic_1f642,"slightly smiling face"),
        Icon1f643(R.drawable.ic_1f643,R.string.ic_1f643,"upside-down face"),
        Icon1fae0(R.drawable.ic_1fae0,R.string.ic_1fae0,"melting face"),
        Icon1f609(R.drawable.ic_1f609,R.string.ic_1f609,"winking face"),
        Icon1f60a(R.drawable.ic_1f60a,R.string.ic_1f60a,"smiling face with smiling eyes"),
        Icon1f607(R.drawable.ic_1f607,R.string.ic_1f607,"smiling face with halo"),
        Icon1f970(R.drawable.ic_1f970,R.string.ic_1f970,"smiling face with hearts"),
        Icon1f60d(R.drawable.ic_1f60d,R.string.ic_1f60d,"smiling face with heart-eyes"),
        Icon1f629(R.drawable.ic_1f629,R.string.ic_1f629,"star-struck"),
        Icon1f618(R.drawable.ic_1f618,R.string.ic_1f618,"face blowing a kiss"),
        Icon1f617(R.drawable.ic_1f617,R.string.ic_1f617,"kissing face"),
        Icon263a (R.drawable.ic_263a,R.string.ic_263a,"smiling face"),
        Icon1f61a(R.drawable.ic_1f61a,R.string.ic_1f61a,"kissing face with closed eyes"),
        Icon1f619(R.drawable.ic_1f619,R.string.ic_1f619,"kissing face with smiling eyes"),
        Icon1f972(R.drawable.ic_1f972,R.string.ic_1f972,"smiling face with tear"),
        Icon1f60b(R.drawable.ic_1f60b,R.string.ic_1f60b,"face savoring food"),
        Icon1f61b(R.drawable.ic_1f61b,R.string.ic_1f61b,"face with tongue"),
        Icon1f61c(R.drawable.ic_1f61c,R.string.ic_1f61c,"winking face with tongue"),
        Icon1f62a(R.drawable.ic_1f62a,R.string.ic_1f62a,"zany face"),
        Icon1f61d(R.drawable.ic_1f61d,R.string.ic_1f61d,"squinting face with tongue");

        private int id;
        private int descr;
        private Emoji(int id,int descr,String txt) {
            this.id=id;
            this.descr=descr;
        }
    }
}
