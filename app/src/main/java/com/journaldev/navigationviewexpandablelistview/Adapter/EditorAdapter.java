package com.journaldev.navigationviewexpandablelistview.Adapter;

import android.graphics.Color;
import android.support.annotation.NonNull;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.widget.HorizontalScrollView;

import com.journaldev.navigationviewexpandablelistview.Model.EditorModel;
import com.journaldev.navigationviewexpandablelistview.R;

import java.util.ArrayList;

import jp.wasabeef.richeditor.RichEditor;


public class EditorAdapter extends RecyclerView.Adapter<RecyclerView.ViewHolder>{

    private ArrayList<EditorModel> mDataset;
    private HorizontalScrollView mMenuView=null;
    private ArrayList<RichEditor> editorSet;

    private RichEditor mEditor;

    @Override
    public int getItemViewType(int position) {
        // Just as an example, return 0 or 2 depending on position
        // Note that unlike in ListView adapters, types don't have to be contiguous
        return mDataset.get(position).getType();
    }


    // Provide a suitable constructor (depends on the kind of dataset)
    public EditorAdapter(HorizontalScrollView mMenuView , RecyclerView mRecyclerList, ArrayList<EditorModel> myDataset, ArrayList<RichEditor> editorSet) {
        this.mMenuView = mMenuView;
        this.mRecyclerList = mRecyclerList;
        this.mDataset = myDataset;
        this.editorSet = editorSet;
    }

    // Create new views (invoked by the layout manager)
    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(ViewGroup parent,
                                                      int viewType) {
        // create a new view
        // 지금 이 시스템으로 하면 무조건 0,2로 구분되서 순환되서 출력될텐데;;;;
        switch (viewType) {
            case 1:
                return new ViewHolder(LayoutInflater.from(parent.getContext())
                        .inflate(R.layout.content_editor, parent, false));   //1번 뷰타입
            case 2:
                return new ViewHolder2(LayoutInflater.from(parent.getContext())
                        .inflate(R.layout.content_editor, parent, false));   //2번 뷰타입
            default: return null; //디폴트 널
        }
    }

    @Override
    public void onBindViewHolder(@NonNull RecyclerView.ViewHolder holder, int position) {
        switch (getItemViewType(position)) {
            case 1:
                mEditor.setHtml(mDataset.get(position).getContent());
                buttonSetting(mMenuView);

                if(position>=1) {
                    editorSet.get(position - 1).getLayoutParams().height = ViewGroup.LayoutParams.WRAP_CONTENT;
                    editorSet.get(position - 1).requestLayout();
                }
                break;
            case 2:
                String imagePath = mDataset.get(position).getContent();
                String html = "<html><head></head><body> <img src=\""+ imagePath + "\" width=\""+ mEditor.getLayoutParams().width +"\" height=\"350\"> </body></html>";
                mEditor.loadDataWithBaseURL("", html, "text/html","utf-8", "");
                mEditor.getLayoutParams().height = ViewGroup.LayoutParams.WRAP_CONTENT; // LayoutParams: android.view.ViewGroup.LayoutParams
                // wv.getLayoutParams().height = LayoutParams.WRAP_CONTENT;
                mEditor.requestLayout();//It is necesary to refresh the screen
                buttonSetting(mMenuView);

                if(position>=1) {
                    editorSet.get(position - 1).getLayoutParams().height = ViewGroup.LayoutParams.WRAP_CONTENT;
                    editorSet.get(position - 1).requestLayout();
                }
                break;
        }
    }
    /*
        // Replace the contents of  a view (invoked by the layout manager)
        @Override
        public void onBindViewHolder(MyViewHolder holder, int position) {
            // - get element from your dataset at this position
            // - replace the contents of the view with that element
            holder.mTextView.setText(mDataset[position]);
        }
    */
    // Return the size of your dataset (invoked by the layout manager)
    @Override
    public int getItemCount() {
        return mDataset.size();
    }

    private int previousPosition = 0;
    private RecyclerView mRecyclerList = null;

    class ViewHolder extends RecyclerView.ViewHolder{
        RichEditor editor = null;
        public ViewHolder(View itemView) {
            super(itemView);
            mEditor = (RichEditor) itemView.findViewById(R.id.editor);
            editor = (RichEditor) itemView.findViewById(R.id.editor);

            editorSet.add(mEditor);

            editor.setOnTouchListener(new View.OnTouchListener() {
                @Override
                public boolean onTouch(View v, MotionEvent event) {
                    View view = null;

                    if (getAdapterPosition() == previousPosition) {
                        //웹뷰가 선택된 곳의 행동처리 (이전과 같은 곳을 클릭할 경우)
                        view = mRecyclerList.findViewHolderForAdapterPosition(previousPosition).itemView;
                        mEditor = (RichEditor) view.findViewById(R.id.editor);
                        //buttonSetting(view);

                        buttonSetting(mMenuView);

                        //mEditor.setBackgroundColor(Color.GREEN);
                        previousPosition = getAdapterPosition();

                    } else {
                        //웹뷰가 선택되지 않은 곳의 행동처리
                        //view = mRecyclerList.findViewHolderForAdapterPosition(previousPosition).itemView;
                        //mEditor = (RTextEditorView) view.findViewById(R.id.editor);
                        //mEditor.setBackgroundColor(Color.WHITE);

                        //웹뷰가 선택된 곳의 행동처리 (이전과 다른 곳을 클릭할 경우)
                        //view = mRecyclerList.findViewHolderForAdapterPosition(getAdapterPosition()).itemView;
                        //mEditor = (RTextEditorView) view.findViewById(R.id.editor);
                        //mEditor.setBackgroundColor(Color.GREEN);
                        previousPosition = getAdapterPosition();
                    }
                    return false;
                }
            });
        }
    }

    class ViewHolder2 extends RecyclerView.ViewHolder{
        RichEditor editor = null;
        public ViewHolder2(View itemView) {
            super(itemView);
            mEditor = (RichEditor) itemView.findViewById(R.id.editor);
            editor = (RichEditor) itemView.findViewById(R.id.editor);

            editorSet.add(mEditor);

            editor.setOnTouchListener(new View.OnTouchListener() {
                @Override
                public boolean onTouch(View v, MotionEvent event) {
                    View view = null;

                    if (getAdapterPosition() == previousPosition) {
                        //웹뷰가 선택된 곳의 행동처리 (이전과 같은 곳을 클릭할 경우)
                        view = mRecyclerList.findViewHolderForAdapterPosition(previousPosition).itemView;
                        mEditor = (RichEditor) view.findViewById(R.id.editor);
                        //buttonSetting(view);

                        buttonSetting(mMenuView);

                        //mEditor.setBackgroundColor(Color.GREEN);
                        previousPosition = getAdapterPosition();

                    } else {
                        //웹뷰가 선택되지 않은 곳의 행동처리
                        //view = mRecyclerList.findViewHolderForAdapterPosition(previousPosition).itemView;
                        //mEditor = (RTextEditorView) view.findViewById(R.id.editor);
                        //mEditor.setBackgroundColor(Color.WHITE);

                        //웹뷰가 선택된 곳의 행동처리 (이전과 다른 곳을 클릭할 경우)
                        //view = mRecyclerList.findViewHolderForAdapterPosition(getAdapterPosition()).itemView;
                        //mEditor = (RTextEditorView) view.findViewById(R.id.editor);
                        //mEditor.setBackgroundColor(Color.GREEN);
                        previousPosition = getAdapterPosition();
                    }
                    return false;
                }
            });
        }
    }

    public void buttonSetting(View view) {

        view.findViewById(R.id.action_undo).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                mEditor.undo();
            }
        });

        view.findViewById(R.id.action_redo).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                mEditor.redo();
            }
        });

        view.findViewById(R.id.action_bold).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                mEditor.setBold();
            }
        });

        view.findViewById(R.id.action_italic).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                mEditor.setItalic();
            }
        });

        view.findViewById(R.id.action_subscript).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                mEditor.setSubscript();
            }
        });

        view.findViewById(R.id.action_superscript).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                mEditor.setSuperscript();
            }
        });

        view.findViewById(R.id.action_strikethrough).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                mEditor.setStrikeThrough();
            }
        });

        view.findViewById(R.id.action_underline).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                mEditor.setUnderline();
            }
        });

        view.findViewById(R.id.action_heading1).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                mEditor.setHeading(1);
            }
        });

        view.findViewById(R.id.action_heading2).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                mEditor.setHeading(2);
            }
        });

        view.findViewById(R.id.action_heading3).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                mEditor.setHeading(3);
            }
        });

        view.findViewById(R.id.action_heading4).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                mEditor.setHeading(4);
            }
        });

        view.findViewById(R.id.action_heading5).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                mEditor.setHeading(5);
            }
        });

        view.findViewById(R.id.action_heading6).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                mEditor.setHeading(6);
            }
        });

        view.findViewById(R.id.action_txt_color).setOnClickListener(new View.OnClickListener() {
            private boolean isChanged;

            @Override
            public void onClick(View v) {
                mEditor.setTextColor(isChanged ? Color.BLACK : Color.RED);
                isChanged = !isChanged;
            }
        });

        view.findViewById(R.id.action_bg_color).setOnClickListener(new View.OnClickListener() {
            private boolean isChanged;

            @Override
            public void onClick(View v) {
                mEditor.setTextBackgroundColor(isChanged ? Color.TRANSPARENT : Color.YELLOW);
                isChanged = !isChanged;
            }
        });

        view.findViewById(R.id.action_indent).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                mEditor.setIndent();
            }
        });

        view.findViewById(R.id.action_outdent).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                mEditor.setOutdent();
            }
        });

        view.findViewById(R.id.action_align_left).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                mEditor.setAlignLeft();
            }
        });

        view.findViewById(R.id.action_align_center).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                mEditor.setAlignCenter();
            }
        });

        view.findViewById(R.id.action_align_right).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                mEditor.setAlignRight();
            }
        });

        view.findViewById(R.id.action_blockquote).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                mEditor.setBlockquote();
            }
        });

        view.findViewById(R.id.action_insert_bullets).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                //mEditor.setBullets();
            }
        });

        view.findViewById(R.id.action_insert_numbers).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                //mEditor.setNumbers();
            }
        });

        view.findViewById(R.id.action_insert_link).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                //mEditor.insertLink("https://github.com/wasabeef", "wasabeef");
            }
        });
        view.findViewById(R.id.action_insert_checkbox).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                //mEditor.insertTodo();
            }
        });
    }

}