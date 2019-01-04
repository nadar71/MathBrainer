package eu.indiewalkabout.mathbrainer;

import android.content.DialogInterface;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;
import android.app.AlertDialog;

import java.util.Random;

public class MathRandomOp_Choose_Activity extends AppCompatActivity {

    // fields
    //private String[] operands = {"+","-","*","%"};
    private int rangeMaxValue = 10;
    private int firstOperandValue;
    private int secondOperandValue;
    private int choosenOperand;
    private int offset = 10;

    TextView firstOperand_choose;
    TextView secondOperand_choose;
    TextView operationSymbol_choose;

    Button answer01Btn;
    Button answer02Btn;
    Button answer03Btn;
    Button answer04Btn;
    Button answer05Btn;
    Button answer06Btn;
    Button answer07Btn;
    Button answer08Btn;
    Button answer09Btn;

    private int correctAnswer = 0;
    private int correctBtnNumber = 1;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_math_randomop_choose);

        // Views references
        firstOperand_choose    =   findViewById(R.id.firstOperand_choose);
        secondOperand_choose   =   findViewById(R.id.secondOperand_choose);
        operationSymbol_choose =   findViewById(R.id.operationSymbol_choose);

        answer01Btn =  findViewById(R.id.answer_01Btn);
        answer02Btn =  findViewById(R.id.answer_02Btn);
        answer03Btn =  findViewById(R.id.answer_03Btn);
        answer04Btn =  findViewById(R.id.answer_04Btn);
        answer05Btn =  findViewById(R.id.answer_05Btn);
        answer06Btn =  findViewById(R.id.answer_06Btn);
        answer07Btn =  findViewById(R.id.answer_07Btn);
        answer08Btn =  findViewById(R.id.answer_08Btn);
        answer09Btn =  findViewById(R.id.answer_09Btn);

        answer01Btn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Button b = (Button) view;
                int btnValue = Integer.parseInt((String)b.getText());
                if (btnValue==correctAnswer) {
                    // Toast.makeText(MathRandomOp_Choose_Activity.this, "OK !", Toast.LENGTH_SHORT).show();
                    setDialog_1Btn("OK ! Next challenge?", "","Yes");
                } else if (btnValue!=correctAnswer) {
                    // Toast.makeText(MathRandomOp_Choose_Activity.this, "....WRONG !!!", Toast.LENGTH_SHORT).show();
                    setDialog_1Btn("...WRONG !!! Next challenge?", "","Yes");
                }

            }
        });


        answer02Btn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Button b = (Button) view;
                int btnValue = Integer.parseInt((String)b.getText());
                if (btnValue==correctAnswer) {
                    // Toast.makeText(MathRandomOp_Choose_Activity.this, "OK !", Toast.LENGTH_SHORT).show();
                    setDialog_1Btn("OK ! Next challenge?", "","Yes");
                } else if (btnValue!=correctAnswer) {
                    // Toast.makeText(MathRandomOp_Choose_Activity.this, "....WRONG !!!", Toast.LENGTH_SHORT).show();
                    setDialog_1Btn("...WRONG !!! Next challenge?", "","Yes");
                }

            }
        });


        answer03Btn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Button b = (Button) view;
                int btnValue = Integer.parseInt((String)b.getText());
                if (btnValue==correctAnswer) {
                    // Toast.makeText(MathRandomOp_Choose_Activity.this, "OK !", Toast.LENGTH_SHORT).show();
                    setDialog_1Btn("OK ! Next challenge?", "","Yes");
                } else if (btnValue!=correctAnswer) {
                    // Toast.makeText(MathRandomOp_Choose_Activity.this, "....WRONG !!!", Toast.LENGTH_SHORT).show();
                    setDialog_1Btn("...WRONG !!! Next challenge?", "","Yes");
                }
                // setChooseMathChallenge();
            }
        });


        answer04Btn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Button b = (Button) view;
                int btnValue = Integer.parseInt((String)b.getText());
                if (btnValue==correctAnswer) {
                    // Toast.makeText(MathRandomOp_Choose_Activity.this, "OK !", Toast.LENGTH_SHORT).show();
                    setDialog_1Btn("OK ! Next challenge?", "","Yes");
                } else if (btnValue!=correctAnswer) {
                    // Toast.makeText(MathRandomOp_Choose_Activity.this, "....WRONG !!!", Toast.LENGTH_SHORT).show();
                    setDialog_1Btn("...WRONG !!! Next challenge?", "","Yes");
                }
                // setChooseMathChallenge();
            }
        });


        answer05Btn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Button b = (Button) view;
                int btnValue = Integer.parseInt((String)b.getText());
                if (btnValue==correctAnswer) {
                    // Toast.makeText(MathRandomOp_Choose_Activity.this, "OK !", Toast.LENGTH_SHORT).show();
                    setDialog_1Btn("OK ! Next challenge?", "","Yes");
                } else if (btnValue!=correctAnswer) {
                    // Toast.makeText(MathRandomOp_Choose_Activity.this, "....WRONG !!!", Toast.LENGTH_SHORT).show();
                    setDialog_1Btn("...WRONG !!! Next challenge?", "","Yes");
                }
                // setChooseMathChallenge();
            }
        });


        answer06Btn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Button b = (Button) view;
                int btnValue = Integer.parseInt((String)b.getText());
                if (btnValue==correctAnswer) {
                    // Toast.makeText(MathRandomOp_Choose_Activity.this, "OK !", Toast.LENGTH_SHORT).show();
                    setDialog_1Btn("OK ! Next challenge?", "","Yes");
                } else if (btnValue!=correctAnswer) {
                    // Toast.makeText(MathRandomOp_Choose_Activity.this, "....WRONG !!!", Toast.LENGTH_SHORT).show();
                    setDialog_1Btn("...WRONG !!! Next challenge?", "","Yes");
                }
                // setChooseMathChallenge();
            }
        });


        answer07Btn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Button b = (Button) view;
                int btnValue = Integer.parseInt((String)b.getText());
                if (btnValue==correctAnswer) {
                    // Toast.makeText(MathRandomOp_Choose_Activity.this, "OK !", Toast.LENGTH_SHORT).show();
                    setDialog_1Btn("OK ! Next challenge?", "","Yes");
                } else if (btnValue!=correctAnswer) {
                    // Toast.makeText(MathRandomOp_Choose_Activity.this, "....WRONG !!!", Toast.LENGTH_SHORT).show();
                    setDialog_1Btn("...WRONG !!! Next challenge?", "","Yes");
                }
                // setChooseMathChallenge();
            }
        });


        answer08Btn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Button b = (Button) view;
                int btnValue = Integer.parseInt((String)b.getText());
                if (btnValue==correctAnswer) {
                    // Toast.makeText(MathRandomOp_Choose_Activity.this, "OK !", Toast.LENGTH_SHORT).show();
                    setDialog_1Btn("OK ! Next challenge?", "","Yes");
                } else if (btnValue!=correctAnswer) {
                    // Toast.makeText(MathRandomOp_Choose_Activity.this, "....WRONG !!!", Toast.LENGTH_SHORT).show();
                    setDialog_1Btn("...WRONG !!! Next challenge?", "","Yes");
                }
                // setChooseMathChallenge();
            }
        });


        answer09Btn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Button b = (Button) view;
                int btnValue = Integer.parseInt((String)b.getText());
                if (btnValue==correctAnswer) {
                    // Toast.makeText(MathRandomOp_Choose_Activity.this, "OK !", Toast.LENGTH_SHORT).show();
                    setDialog_1Btn("OK ! Next challenge?", "","Yes");
                } else if (btnValue!=correctAnswer) {
                    // Toast.makeText(MathRandomOp_Choose_Activity.this, "....WRONG !!!", Toast.LENGTH_SHORT).show();
                    setDialog_1Btn("...WRONG !!! Next challenge?", "","Yes");
                }
                // setChooseMathChallenge();
            }
        });


        //start the 1st math challenge
        setChooseMathChallenge();


    }


    /**
     * create a math challenge
     */
    void setChooseMathChallenge() {
        Random r = new Random();
        // set operations btn text
        firstOperandValue = r.nextInt(rangeMaxValue)+1;
        choosenOperand = r.nextInt(4 )+1;
        while ( (secondOperandValue == 0) &&(choosenOperand==4) ){  //avoid division by 0; choosenOperand==4 means equal to '/'
               secondOperandValue = r.nextInt(rangeMaxValue) + 1;
        }

        firstOperand_choose.setText(String.valueOf(firstOperandValue));
        secondOperand_choose.setText(String.valueOf(secondOperandValue));



        // set current answer
        switch(choosenOperand){

            case 1 : //+
                correctAnswer = firstOperandValue + secondOperandValue;
                operationSymbol_choose.setText("+");
                Log.i("RESULT : ",correctAnswer + " =  "+ firstOperandValue + "+"+secondOperandValue );
                break;

            case 2 : //-
                correctAnswer = firstOperandValue - secondOperandValue;
                operationSymbol_choose.setText("-");
                Log.i("RESULT : ",correctAnswer + " =  "+ firstOperandValue + "-"+secondOperandValue );
                break;

            case 3 : //*
                correctAnswer = firstOperandValue * secondOperandValue;
                operationSymbol_choose.setText("X");
                Log.i("RESULT : ",correctAnswer + " =  "+ firstOperandValue + "*"+secondOperandValue );
                break;

            case 4 : // /
                correctAnswer = ( firstOperandValue / secondOperandValue);
                operationSymbol_choose.setText(":");
                Log.i("RESULT : ",correctAnswer + " =  "+ firstOperandValue + "/"+secondOperandValue );
                break;

            default:
                break;
        }

        // choose the button where put the correct answer
        correctBtnNumber = r.nextInt(9)+1;
        Log.i("OK answer Btn number : ",String.valueOf(correctBtnNumber));
        Button tmpBtn = getTheBtnNumber(correctBtnNumber);
        Log.i("OK answer Btn ptr : ",String.valueOf(tmpBtn));
        tmpBtn.setText(String.valueOf(correctAnswer));
        Log.i("OK answer set : ",String.valueOf(tmpBtn));

        // set wrong answer on the others
        for(int i=1;i<=9;i++){
            if (i!=correctBtnNumber){
                tmpBtn = getTheBtnNumber(i);
                Log.i("Button ptr  choosen : ",String.valueOf(tmpBtn));
                tmpBtn.setText(String.valueOf(correctAnswer+r.nextInt(offset)+2));
                Log.i("Text set on button : ",String.valueOf(tmpBtn));
            }
        }


    }


    /**
     * Return the button based on number
     * @param num
     * @return
     */
    Button getTheBtnNumber(int num){
      switch(num){
          case 1 : return answer01Btn;
          case 2 : return answer02Btn;
          case 3 : return answer03Btn;
          case 4 : return answer04Btn;
          case 5 : return answer05Btn;
          case 6 : return answer06Btn;
          case 7 : return answer07Btn;
          case 8 : return answer08Btn;
          case 9 : return answer09Btn;
          default: break;
      }
      return null;
    }



    void setDialog_1Btn(String title, String msg,String btnText){
        AlertDialog alertDialog = new AlertDialog.Builder(MathRandomOp_Choose_Activity.this)
                //set icon
                .setIcon(android.R.drawable.ic_dialog_alert)
                //set title
                .setTitle(title)
                //set message
                .setMessage(msg)
                //set positive button
                .setPositiveButton(btnText, new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialogInterface, int i) {
                        //set what would happen when positive button is clicked
                        setChooseMathChallenge();
                    }
                })
                            /*
                            //set negative button
                            .setNegativeButton("No", new DialogInterface.OnClickListener() {
                                @Override
                                public void onClick(DialogInterface dialogInterface, int i) {
                                    //set what should happen when negative button is clicked
                                    Toast.makeText(getApplicationContext(),"Nothing Happened",Toast.LENGTH_LONG).show();
                                }
                            })*/
                .show();
    }
    // check timer

    //

}
