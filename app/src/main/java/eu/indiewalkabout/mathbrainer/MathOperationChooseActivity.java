package eu.indiewalkabout.mathbrainer;

import android.content.DialogInterface;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;
import android.app.AlertDialog;

import java.util.Random;

public class MathOperationChooseActivity extends AppCompatActivity {

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

    Button _answer_01Btn;
    Button _answer_02Btn;
    Button _answer_03Btn;
    Button _answer_04Btn;
    Button _answer_05Btn;
    Button _answer_06Btn;
    Button _answer_07Btn;
    Button _answer_08Btn;
    Button _answer_09Btn;

    private int correctAnswer = 0;
    private int correctBtnNumber = 1;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_math_operation_choose);

        // Views references
        firstOperand_choose    =   findViewById(R.id.firstOperand_choose);
        secondOperand_choose   =   findViewById(R.id.secondOperand_choose);
        operationSymbol_choose =   findViewById(R.id.operationSymbol_choose);

        _answer_01Btn =  findViewById(R.id.answer_01Btn);
        _answer_02Btn =  findViewById(R.id.answer_02Btn);
        _answer_03Btn =  findViewById(R.id.answer_03Btn);
        _answer_04Btn =  findViewById(R.id.answer_04Btn);
        _answer_05Btn =  findViewById(R.id.answer_05Btn);
        _answer_06Btn =  findViewById(R.id.answer_06Btn);
        _answer_07Btn =  findViewById(R.id.answer_07Btn);
        _answer_08Btn =  findViewById(R.id.answer_08Btn);
        _answer_09Btn =  findViewById(R.id.answer_09Btn);

        _answer_01Btn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Button b = (Button) view;
                int btnValue = Integer.parseInt((String)b.getText());
                if (btnValue==correctAnswer) {
                    // Toast.makeText(MathOperationChooseActivity.this, "OK !", Toast.LENGTH_SHORT).show();
                    setDialog_1Btn("OK ! Next challenge?", "","Yes");
                } else if (btnValue!=correctAnswer) {
                    // Toast.makeText(MathOperationChooseActivity.this, "....WRONG !!!", Toast.LENGTH_SHORT).show();
                    setDialog_1Btn("...WRONG !!! Next challenge?", "","Yes");
                }

            }
        });


        _answer_02Btn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Button b = (Button) view;
                int btnValue = Integer.parseInt((String)b.getText());
                if (btnValue==correctAnswer) {
                    // Toast.makeText(MathOperationChooseActivity.this, "OK !", Toast.LENGTH_SHORT).show();
                    setDialog_1Btn("OK ! Next challenge?", "","Yes");
                } else if (btnValue!=correctAnswer) {
                    // Toast.makeText(MathOperationChooseActivity.this, "....WRONG !!!", Toast.LENGTH_SHORT).show();
                    setDialog_1Btn("...WRONG !!! Next challenge?", "","Yes");
                }

            }
        });


        _answer_03Btn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Button b = (Button) view;
                int btnValue = Integer.parseInt((String)b.getText());
                if (btnValue==correctAnswer) {
                    // Toast.makeText(MathOperationChooseActivity.this, "OK !", Toast.LENGTH_SHORT).show();
                    setDialog_1Btn("OK ! Next challenge?", "","Yes");
                } else if (btnValue!=correctAnswer) {
                    // Toast.makeText(MathOperationChooseActivity.this, "....WRONG !!!", Toast.LENGTH_SHORT).show();
                    setDialog_1Btn("...WRONG !!! Next challenge?", "","Yes");
                }
                // setChooseMathChallenge();
            }
        });


        _answer_04Btn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Button b = (Button) view;
                int btnValue = Integer.parseInt((String)b.getText());
                if (btnValue==correctAnswer) {
                    // Toast.makeText(MathOperationChooseActivity.this, "OK !", Toast.LENGTH_SHORT).show();
                    setDialog_1Btn("OK ! Next challenge?", "","Yes");
                } else if (btnValue!=correctAnswer) {
                    // Toast.makeText(MathOperationChooseActivity.this, "....WRONG !!!", Toast.LENGTH_SHORT).show();
                    setDialog_1Btn("...WRONG !!! Next challenge?", "","Yes");
                }
                // setChooseMathChallenge();
            }
        });


        _answer_05Btn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Button b = (Button) view;
                int btnValue = Integer.parseInt((String)b.getText());
                if (btnValue==correctAnswer) {
                    // Toast.makeText(MathOperationChooseActivity.this, "OK !", Toast.LENGTH_SHORT).show();
                    setDialog_1Btn("OK ! Next challenge?", "","Yes");
                } else if (btnValue!=correctAnswer) {
                    // Toast.makeText(MathOperationChooseActivity.this, "....WRONG !!!", Toast.LENGTH_SHORT).show();
                    setDialog_1Btn("...WRONG !!! Next challenge?", "","Yes");
                }
                // setChooseMathChallenge();
            }
        });


        _answer_06Btn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Button b = (Button) view;
                int btnValue = Integer.parseInt((String)b.getText());
                if (btnValue==correctAnswer) {
                    // Toast.makeText(MathOperationChooseActivity.this, "OK !", Toast.LENGTH_SHORT).show();
                    setDialog_1Btn("OK ! Next challenge?", "","Yes");
                } else if (btnValue!=correctAnswer) {
                    // Toast.makeText(MathOperationChooseActivity.this, "....WRONG !!!", Toast.LENGTH_SHORT).show();
                    setDialog_1Btn("...WRONG !!! Next challenge?", "","Yes");
                }
                // setChooseMathChallenge();
            }
        });


        _answer_07Btn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Button b = (Button) view;
                int btnValue = Integer.parseInt((String)b.getText());
                if (btnValue==correctAnswer) {
                    // Toast.makeText(MathOperationChooseActivity.this, "OK !", Toast.LENGTH_SHORT).show();
                    setDialog_1Btn("OK ! Next challenge?", "","Yes");
                } else if (btnValue!=correctAnswer) {
                    // Toast.makeText(MathOperationChooseActivity.this, "....WRONG !!!", Toast.LENGTH_SHORT).show();
                    setDialog_1Btn("...WRONG !!! Next challenge?", "","Yes");
                }
                // setChooseMathChallenge();
            }
        });


        _answer_08Btn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Button b = (Button) view;
                int btnValue = Integer.parseInt((String)b.getText());
                if (btnValue==correctAnswer) {
                    // Toast.makeText(MathOperationChooseActivity.this, "OK !", Toast.LENGTH_SHORT).show();
                    setDialog_1Btn("OK ! Next challenge?", "","Yes");
                } else if (btnValue!=correctAnswer) {
                    // Toast.makeText(MathOperationChooseActivity.this, "....WRONG !!!", Toast.LENGTH_SHORT).show();
                    setDialog_1Btn("...WRONG !!! Next challenge?", "","Yes");
                }
                // setChooseMathChallenge();
            }
        });


        _answer_09Btn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Button b = (Button) view;
                int btnValue = Integer.parseInt((String)b.getText());
                if (btnValue==correctAnswer) {
                    // Toast.makeText(MathOperationChooseActivity.this, "OK !", Toast.LENGTH_SHORT).show();
                    setDialog_1Btn("OK ! Next challenge?", "","Yes");
                } else if (btnValue!=correctAnswer) {
                    // Toast.makeText(MathOperationChooseActivity.this, "....WRONG !!!", Toast.LENGTH_SHORT).show();
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
          case 1 : return _answer_01Btn;
          case 2 : return _answer_02Btn;
          case 3 : return _answer_03Btn;
          case 4 : return _answer_04Btn;
          case 5 : return _answer_05Btn;
          case 6 : return _answer_06Btn;
          case 7 : return _answer_07Btn;
          case 8 : return _answer_08Btn;
          case 9 : return _answer_09Btn;
          default: break;
      }
      return null;
    }



    void setDialog_1Btn(String title, String msg,String btnText){
        AlertDialog alertDialog = new AlertDialog.Builder(MathOperationChooseActivity.this)
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
