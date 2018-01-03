package com.example.bagle.app4h;

import android.content.Context;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.print.PrintAttributes;
import android.print.PrintDocumentAdapter;
import android.print.PrintManager;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.View;
import android.webkit.WebView;
import android.webkit.WebViewClient;
import android.widget.Button;

import com.google.firebase.database.ChildEventListener;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;
import java.util.Locale;

//Todo put in checks to make sure that items are handled correctly if they are null;
public class GeneratePDF extends AppCompatActivity {
    //pdf generation
    private static final String TAG = "GENERATE_PDF";
    private WebView myWebView;

    //userInfo and database
    private DatabaseReference projectRef;
    private DatabaseReference projectInfoRef;
    private DatabaseReference userRef;
    private FirebaseDatabase database;
    private String projectName;
    private String firebaseUserId;

    //html page
    private String head;
    private String body;
    private String titlePage1;
    private String userInfo2;
    private String projectTitlePage3;
    private String projectInfo4;
    private String financeRecords5;
    private String goals6;
    private String healthRecord7;
    private String weightRecord8;
    private String meetings9;
    private String leadership10;

    //other variables
    //user information
    private String userFullName;
    private String userBirthday;
    private String userYearIn4H;

    //general project information
    private String projectStartDate;
    private String projectEndDate;
    private String projectType;
    private String projectYears;
    private String projectBalance;
    private String projectMeetingsNumber;
    private String projectGoalsNumber;

    //project Records records
    private List<String> financeRecords;
    private List<String> goals;
    private List<String> meetings;
    private List<String> leaderships;
    private List<String> animalHealths;
    private List<String> animalWeights;

    //other records variables
    private int numberGoals;

    private void compileHTML() {
        // title page 1
        String title = generateHtml("Project Record Book", "h1", "sectionHeading textCenter");
        title = generateHtml(title, "div", "col-md-9");
        String motto = generateHtml("Learn by Doing", "h4","italic textCenter");
        String clover = generateHtml("<img style=\"margin:auto; width:1in; height:1in;\" src=\"clover" +
                ".png\"/>", "div", "col-md-3");
        String header = generateHtml(clover + title,"div", "row");
        header = generateHtml(header, "div", "container");
        String name = generateHtml(userFullName,"h2","textCenter");
        titlePage1 = generateHtml(header +motto + name, "div", "page");

        //user info 2
        String userInfoTitle = generateHtml("Participant Information", "h3", "sectionHeading");
        String fullName = generateHtml("Name: " + userFullName, "h5", "");
        String birthday = generateHtml("Birthday: " +userBirthday, "h5", "");
        String yearsIn4H = generateHtml("Years in 4-H: "+ userYearIn4H, "h5", "");
        userInfo2 = generateHtml(userInfoTitle + fullName+ birthday+yearsIn4H, "div","page");

        //project title page 3
        String projectPageTitle = generateHtml("Project: " +projectName, "h3", "sectionHeading");
        String startDate = generateHtml("Start Date: "+ projectStartDate, "h5", "");
        String endDate = generateHtml("End Date: "+ projectEndDate, "h5", "");
        String type = generateHtml("Project Type: "+ projectType, "h5", "");
        String yearsInProject = generateHtml("Years in Project: " +projectYears, "h5","");
        String totalGoals = generateHtml("Project Goals: " +projectGoalsNumber, "h5","");
        String totalMeetings = generateHtml("Project Meetings: " +projectMeetingsNumber, "h5","");
        String balance = generateHtml("Finance Balance: " +projectBalance, "h5","");

        projectTitlePage3 = generateHtml(projectPageTitle +
                startDate+endDate+type+yearsInProject+totalGoals+totalMeetings+balance, "div",
                "page");

        // projectinfo 4

//        //finance records 5
        StringBuilder allFinances= new StringBuilder();
        for (String record:financeRecords) {
            allFinances.append(record);
        }
        String financeTitle = generateHtml("Finance Records","h3","sectionHeading");
        String financeDescription = generateHtml("Description", "div", "record");
        String financeAmount = generateHtml("Amount: income or (expense)", "div", "record");
        String financeFirstRow = generateHtml(financeDescription +financeAmount, "div","row");

        String financeList = generateHtml(allFinances.toString(),"div", "container");
        String financeEndBalance = generateHtml("Final Balance: " + projectBalance, "div","tableHeading " +
                "row");

        financeRecords5 = generateHtml(financeTitle+ financeFirstRow+financeList+ financeEndBalance,"div",
                "");

        // goals 6
        StringBuilder allGoals = new StringBuilder();
        for (String record:goals) {
            allGoals.append(record);
        }
        String goalTitle = generateHtml("Project Goals", "h3", "sectionHeading");
        goals6 = generateHtml(goalTitle+ allGoals, "div","");

        // meetings 9
        StringBuilder allMeetings = new StringBuilder();
        for(String record:meetings){
            allMeetings.append(record);
        }
        String meetingsTitle = generateHtml("Project Meetings and Activities","h3",
                "sectionHeading");
        meetings9 = generateHtml(meetingsTitle + allMeetings, "div","");
        body = generateHtml(titlePage1 + "<hr>"+userInfo2 + "<hr>" + projectTitlePage3
                        +"<hr>"+financeRecords5+"<hr>"+goals6+"<hr>"+meetings9, "div","");
    }


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_generate_pdf);

        //initialize variables
        getStartUserInfo();
        getDatabaseRef();

        getHead();
        getUserInfo();
        getGeneralProjectInfo();
        getFinanceRecords();
        getGoals();
        getMeetings();
        //getTitlePage();
        //getProjectRecords();
        //initialize UI
        Button button = (Button) findViewById(R.id.button1);
        button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                compileHTML();

                generatePrintJob();
            }
        });
    }




    private void getDatabaseRef() {
        database = FirebaseDatabase.getInstance();
        userRef = database.getReference("users").child(firebaseUserId);
        projectRef = database.getReference("users").child(firebaseUserId).child
                ("projects").child(projectName);
    }

    private void getStartUserInfo() {
        //get project name from previous activity and set the activity title
        SharedPreferences sharedPreferences = getSharedPreferences("userInfo", Context
                .MODE_PRIVATE);
        projectName = sharedPreferences.getString("currentProject", "");
        Log.d("PROJECT NAME: ", projectName);
        firebaseUserId = sharedPreferences.getString("firebaseUserId", "");
    }

    private void createWebPrintJob(WebView webView) {

        PrintManager printManager = (PrintManager) this
                .getSystemService(Context.PRINT_SERVICE);

        PrintDocumentAdapter printAdapter =
                webView.createPrintDocumentAdapter("4-H Project");

        String jobName = getString(R.string.app_name) + " Print Test";

        if (printManager != null) {
            printManager.print(jobName, printAdapter,
                    new PrintAttributes.Builder().build());
        }
    }


    private void generatePrintJob() {
        WebView webView = new WebView(this);
        webView.setWebViewClient(new WebViewClient() {

            public boolean shouldOverrideUrlLoading(WebView view, String url) {
                return false;
            }

            @Override
            public void onPageFinished(WebView view, String url) {
                createWebPrintJob(view);
                myWebView = null;
            }
        });

        webView.loadDataWithBaseURL("https://bagleric.github.io/seniorProject/",
                "<html><head>" + head +"</head><body>" + body + "</body></html>" ,"text/HTML",
                "UTF-8", null);
        myWebView = webView;
    }

//
//    private void getHtml() {
//        getHead();
//        getBody();
//        htmlDocument = generateHtml(head + body, "html", "");
//    }

    public void getHtmlDocument() {

    }

    public void getHead() {
        String mainCss = "<link rel=\"stylesheet\" href=\"./main.css\">";
        String bootstrap = "<link rel=\"stylesheet\"href=\"https://maxcdn.bootstrapcdn" +
                ".com/bootstrap/4.0.0-beta.2/css/bootstrap.min.css\" integrity=\"sha384-PsH8R72JQ3SOdhVi3uxftmaW6Vc51MKb0q5P2rRUpPvrszuE4W1povHYgTpBfshb\" crossorigin=\"anonymous\">";
        head = generateHtml(mainCss + bootstrap, "head", "");
        Log.d("TAG", "HEAD: " +head);
    }

//    public void getBody() {
//        //getTitlePage();
////        getFourHInfo();
////        getGoals();
////        String totalBalance = generateHtml("Balance: " + financeRecordBalance,"div","row");
////        financeRecords += totalBalance;
////        financeRecords = generateHtml(financeRecords, "div", "container");
////        getMeetings();
////        getHealthRecords();
////        getWeight();
////        getLeadership();
//        body = generateHtml(titlePage, "body", "");
//        //body = generateHtml(titlePage + projectTitlePage + projectInfo + financeRecords + goals +
//         //       healthRecord + weightRecord + meetings + leadership, "body", "");
//        Log.d("TAG", "Body: " +body);
//    }

    public void getHtmlTitle() {
    }
//
//    public void getTitlePage() {
//        String recordBookTitle = generateHtml(projectName,"h1", "sectionHeading textCenter");
//        Calendar calendar = Calendar.getInstance();
//        String year = String.valueOf(calendar.get(Calendar.YEAR));
//        year = generateHtml(year, "h4", "italic textCenter");
//        Log.d("TAG", "NAMEs: " +projectName);
//
//        title = generateHtml(name + year, "div", "");
//        titlePage =generateHtml(title, "div", "page");
//        Log.d("TAG", "Title: " +title);
//        Log.d("TAG", "TitlePAge: " +titlePage);
//    }

//    public void getFourHInfo() {
//        projectInfoRef.addListenerForSingleValueEvent(new ValueEventListener() {
//            @Override
//            public void onDataChange(DataSnapshot dataSnapshot) {
//
//                String dueDate;
//                String startDate;
//                String projectName;
//                String numGoals;
//                String numMeetings;
//                String yearsInProject;
//                String projectType;
//                if (dataSnapshot.child("dueDate").getValue(String.class) == null
//                        || dataSnapshot.child("numberGoals").getValue(long.class) == null
//                        || dataSnapshot.child("numberMeetings").getValue(long.class) == null
//                        || dataSnapshot.child("projectName").getValue(String.class) == null
//                        || dataSnapshot.child("projectType").getValue(String.class) == null
//                        || dataSnapshot.child("startDate").getValue(String.class) == null
//                        || dataSnapshot.child("yearsInProject").getValue(String.class) == null) {
//                    return;
//                }
//                dueDate = dataSnapshot.child("dueDate").getValue(String.class);
//                startDate = dataSnapshot.child("startDate").getValue(String.class);
//                projectName = dataSnapshot.child("projectName").getValue(String.class);
//                yearsInProject = dataSnapshot.child("yearsInProject").getValue(String.class);
//                numGoals = String.valueOf(dataSnapshot.child("numberGoals").getValue(long.class));
//                numMeetings = String.valueOf(dataSnapshot.child("numberMeetings").getValue(long
//                        .class));
//                projectType = dataSnapshot.child("projectType").getValue(String.class);
//
//                dueDate = generateHtml("End Date: " + dueDate, "p", "");
//                startDate = generateHtml("Start Date: " + startDate, "p", "");
//                projectName = generateHtml("Project Name: " + projectName, "p", "");
//                numGoals = generateHtml("Total Goals: " + numGoals, "p", "");
//                numMeetings = generateHtml("Total Meetings and Activities: " + numMeetings, "p", "");
//                yearsInProject = generateHtml("Years in Project: " + yearsInProject, "p", "");
//                projectType = generateHtml("Project Type: " + projectType, "p", "");
//
//                String title = generateHtml("Project Information", "h2", "sectionHeading");
//                projectInfo = generateHtml(title + projectName + startDate + dueDate + projectType +
//                        yearsInProject + numGoals + numMeetings, "div", "");
//                Log.d("TAG", "PRoject Info: " +projectInfo);
//
//            }
//
//            @Override
//            public void onCancelled(DatabaseError databaseError) {
//
//            }
//        });
//    }
//
//    public void getProjectTitlePage() {
//        projectTitlePage = generateHtml(projectName, "h1","sectionHeading");
//    }
//
    public void getFinanceRecords() {
        financeRecords = new ArrayList<String>();
        DatabaseReference projectFinanceRef = projectRef.child("financeRecords");
        projectFinanceRef.addChildEventListener(new ChildEventListener() {
            @Override

            public void onChildAdded(DataSnapshot dataSnapshot, String s) {
                String description = "";
                String strBalance = "";
                BigDecimal amount = new BigDecimal(0);
                boolean isExpense = false;
                if (dataSnapshot.child("description").getValue() == null
                        || dataSnapshot.child("amount").getValue() == null
                        || dataSnapshot.child("isExpense").getValue() == null) {
                    return;
                }
                description = dataSnapshot.child("description").getValue(String.class);
                amount = new BigDecimal(dataSnapshot.child("amount").getValue(String.class));
                Object object = dataSnapshot.child("isExpense").getValue();
                if (object != null) {
                    if (object.toString().equals("true")) {
                        strBalance = " $ " + String.valueOf(amount.setScale(2, RoundingMode
                                .HALF_UP))+ " ";

                        isExpense = true;
                    } else if (object.toString().equals("false")) {
                        strBalance = "($ " + String.valueOf(amount.setScale(2, RoundingMode
                                .HALF_UP))+ ")";

                        isExpense = false;
                    }
                }
//                if (balance.compareTo(BigDecimal.ZERO)  0){
//                    strBalance = "("+ strBalance + ")";
//                }


                description = generateHtml(description,"div","record");
                strBalance = generateHtml(strBalance,"div","record");
                String divider = generateHtml("", "div", "divider");
                String financeRecord= generateHtml(description+strBalance + divider,
                        "div","row");
                financeRecords.add(financeRecord);
                Log.d("TAG", "FinanceRecords: " +financeRecord);

            }

            @Override
            public void onChildChanged(DataSnapshot dataSnapshot, String s) {

            }

            @Override
            public void onChildRemoved(DataSnapshot dataSnapshot) {
            }

            @Override
            public void onChildMoved(DataSnapshot dataSnapshot, String s) {

            }

            @Override
            public void onCancelled(DatabaseError databaseError) {
            }
        });
    }

    public void getMeetings() {
        meetings = new ArrayList<String>();
        projectRef.child("meetings").orderByChild("meetingDate").addChildEventListener(new ChildEventListener
                () {
            @Override
            public void onChildAdded(DataSnapshot dataSnapshot, String s) {
                String description = "";
                long meetingDate = 0;
                if (dataSnapshot.child("description").getValue() == null
                        || dataSnapshot.child("meetingDate").getValue() == null) {
                    return;
                }
                description = dataSnapshot.child("description").getValue(String.class);
                if (dataSnapshot.child("meetingDate").getValue(Long.class) != null) {
                    meetingDate = dataSnapshot.child("meetingDate").getValue(Long.class);
                }

                Calendar myCal = Calendar.getInstance();
                myCal.setTimeInMillis(meetingDate);
                String date = new SimpleDateFormat("E, MMM d, yyyy", Locale.US).format
                        (myCal.getTime());

                description = generateHtml(description,"div","record");
                date = generateHtml(date,"div","record");
                String record = generateHtml(date + description,"div","row");
                meetings.add(record);
            }

            @Override
            public void onChildChanged(DataSnapshot dataSnapshot, String s) {

            }

            @Override
            public void onChildRemoved(DataSnapshot dataSnapshot) {

            }

            @Override
            public void onChildMoved(DataSnapshot dataSnapshot, String s) {

            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        });
    }

    public void getGoals() {
        goals = new ArrayList<String>();
        numberGoals=0;
        projectRef.child("goals").addChildEventListener(new ChildEventListener() {
            @Override

            public void onChildAdded(DataSnapshot dataSnapshot, String s) {
                String description = "";
                String todos = "";
                long deadline = 0;
                if (dataSnapshot.child("description").getValue() == null
                        || dataSnapshot.child("toDos").getValue() == null
                        || dataSnapshot.child("deadline").getValue() == null) {
                    return;
                }
                description = dataSnapshot.child("description").getValue(String.class);
                todos = dataSnapshot.child("toDos").getValue(String.class);
                if (dataSnapshot.child("deadline").getValue(Long.class) != null) {
                    deadline = dataSnapshot.child("deadline").getValue(Long.class);
                }

                Calendar myCal = Calendar.getInstance();
                        myCal.setTimeInMillis(deadline);
                String theDeadline = new SimpleDateFormat("E, MMM d, yyyy", Locale.US).format
                        (myCal.getTime());
                numberGoals++;
                String goalTitle = generateHtml("Goal #" + numberGoals, "h4", "italic");
                description = generateHtml(description, "p", "indent1");
                theDeadline = generateHtml("Deadline: "+ theDeadline, "p", "indent1");
                todos = generateHtml(todos, "p", "indent2");
                String theGoal = generateHtml(goalTitle + description +theDeadline+ todos, "div", "");
                goals.add(theGoal);
            }

            @Override
            public void onChildChanged(DataSnapshot dataSnapshot, String s) {

            }

            @Override
            public void onChildRemoved(DataSnapshot dataSnapshot) {
            }

            @Override
            public void onChildMoved(DataSnapshot dataSnapshot, String s) {

            }

            @Override
            public void onCancelled(DatabaseError databaseError) {
            }
        });
    }
//
//    public void getHealthRecords() {
//        healthRecord = generateHtml("No healthRecord", "h3","");
//
//    }
//
//    public void getLeadership() {
//        leadership = generateHtml("No leadership", "h3","");
//    }
//
//    public void getWeight() {
//        weightRecord =generateHtml("No weights", "h3","");
//    }

    public void getUserInfo() {
        userRef.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                String birthday;
                String yearIn4H;

                if (dataSnapshot.child("firstName").getValue(String.class) == null
                        || dataSnapshot.child("lastName").getValue(String.class) == null
                        || dataSnapshot.child("yearIn4H").getValue(String.class) == null
                        || dataSnapshot.child("birthday").getValue(String.class) == null) {

                    return;

                }
                userFullName = dataSnapshot.child("firstName").getValue(String.class) + " " +
                        dataSnapshot.child("lastName").getValue(String.class);
                userBirthday = dataSnapshot.child("birthday").getValue(String.class);
                userYearIn4H = dataSnapshot.child("yearIn4H").getValue(String.class);
//                String name = generateHtml(firstName + " " + lastName, "p", "");
//                String bday = generateHtml("Birthday: " + birthday, "p", "");
//                String y4H = generateHtml("Years in 4-H: " + yearIn4H, "p", "");
//
//                userInfo = generateHtml(name + bday + y4H, "div", "");
//
//                Log.d("TAG", "INFORMATION: " +firstName + lastName + birthday + yearIn4H + "="
//                        +name + bday + y4H);
//
//
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        });
        return;
    }

    public String generateHtml(String body, String container, String classes) {
        String html = "";
        if(classes.equals("")){
            html = "<" + container + ">" + body + "</" + container + ">";
        }
        else
        {
            html = "<" + container + " class=\"" + classes + "\">" + body + "</" + container + ">";
        }
        return html;
    }

    public void getGeneralProjectInfo() {
        projectRef.child("info").addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
//                if (dataSnapshot.child("dueDate").getValue(String.class) == null
//                        || dataSnapshot.child("numberGoals").getValue(long.class) == null
//                        || dataSnapshot.child("numberMeetings").getValue(long.class) == null
//                        || dataSnapshot.child("projectName").getValue(String.class) == null
//                        || dataSnapshot.child("projectType").getValue(String.class) == null
//                        || dataSnapshot.child("startDate").getValue(String.class) == null
//                        || dataSnapshot.child("yearsInProject").getValue(String.class) == null) {
//                    Log.d("GENERAL INFO", "ERROR");
//                    return;
//                }
                projectEndDate = dataSnapshot.child("dueDate").getValue(String.class);
                projectStartDate = dataSnapshot.child("startDate").getValue(String.class);
                projectYears = dataSnapshot.child("yearsInProject").getValue(String.class);
                projectGoalsNumber = String.valueOf(dataSnapshot.child("numberGoals").getValue(long.class));
                projectMeetingsNumber = String.valueOf(dataSnapshot.child("numberMeetings").getValue(long
                        .class));
                projectType = dataSnapshot.child("projectType").getValue(String.class);
                projectBalance = dataSnapshot.child("balance").getValue(String.class);

//
//                 = generateHtml("End Date: " + dueDate, "p", "");
//                startDate = generateHtml("Start Date: " + startDate, "p", "");
//                projectName = generateHtml("Project Name: " + projectName, "p", "");
//                numGoals = generateHtml("Total Goals: " + numGoals, "p", "");
//                numMeetings = generateHtml("Total Meetings and Activities: " + numMeetings, "p", "");
//                yearsInProject = generateHtml("Years in Project: " + yearsInProject, "p", "");
//                projectType = generateHtml("Project Type: " + projectType, "p", "");
//
//                String title = generateHtml("Project Information", "h2", "sectionHeading");
//                projectInfo = generateHtml(title + projectName + startDate + dueDate + projectType +
//                        yearsInProject + numGoals + numMeetings, "div", "");
                Log.d("TAG", "PRoject Info: " +projectEndDate +
                        projectStartDate+projectYears+projectGoalsNumber+projectMeetingsNumber
                        +projectType);
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        });

//        projectInfoRef.addChildEventListener(new ChildEventListener() {
//            @Override
//            public void onChildAdded(DataSnapshot dataSnapshot, String s) {
//
//                if (dataSnapshot.child("dueDate").getValue(String.class) == null
//                        || dataSnapshot.child("numberGoals").getValue(long.class) == null
//                        || dataSnapshot.child("numberMeetings").getValue(long.class) == null
//                        || dataSnapshot.child("projectName").getValue(String.class) == null
//                        || dataSnapshot.child("projectType").getValue(String.class) == null
//                        || dataSnapshot.child("startDate").getValue(String.class) == null
//                        || dataSnapshot.child("yearsInProject").getValue(String.class) == null) {
//                    Log.d("GENERAL INFO", "ERROR");
//                    return;
//                }
//                projectEndDate = dataSnapshot.child("dueDate").getValue(String.class);
//                projectStartDate = dataSnapshot.child("startDate").getValue(String.class);
//                projectYears = dataSnapshot.child("yearsInProject").getValue(String.class);
//                projectGoalsNumber = String.valueOf(dataSnapshot.child("numberGoals").getValue(long.class));
//                projectMeetingsNumber = String.valueOf(dataSnapshot.child("numberMeetings").getValue(long
//                        .class));
//                projectType = dataSnapshot.child("projectType").getValue(String.class);
////
////                 = generateHtml("End Date: " + dueDate, "p", "");
////                startDate = generateHtml("Start Date: " + startDate, "p", "");
////                projectName = generateHtml("Project Name: " + projectName, "p", "");
////                numGoals = generateHtml("Total Goals: " + numGoals, "p", "");
////                numMeetings = generateHtml("Total Meetings and Activities: " + numMeetings, "p", "");
////                yearsInProject = generateHtml("Years in Project: " + yearsInProject, "p", "");
////                projectType = generateHtml("Project Type: " + projectType, "p", "");
////
////                String title = generateHtml("Project Information", "h2", "sectionHeading");
////                projectInfo = generateHtml(title + projectName + startDate + dueDate + projectType +
////                        yearsInProject + numGoals + numMeetings, "div", "");
//                Log.d("TAG", "PRoject Info: " +projectEndDate +
//                        projectStartDate+projectYears+projectGoalsNumber+projectMeetingsNumber
//                        +projectType);
//                Toast.makeText(GeneratePDF.this, "projectType", Toast.LENGTH_SHORT).show();
//            }
//
//            @Override
//            public void onChildChanged(DataSnapshot dataSnapshot, String s) {
//
//            }
//
//            @Override
//            public void onChildRemoved(DataSnapshot dataSnapshot) {
//
//            }
//
//            @Override
//            public void onChildMoved(DataSnapshot dataSnapshot, String s) {
//
//            }
//
//            @Override
//            public void onCancelled(DatabaseError databaseError) {
//
//            }
//        });

    }
//
//    public void getProjectRecords() {
//        DatabaseReference financeRef = projectRef.child("financeRecords");
//        financeRef.addChildEventListener(new ChildEventListener() {
//            @Override
//            public void onChildAdded(DataSnapshot dataSnapshot, String s) {
//                balance = new BigDecimal(0);
//                BigDecimal amount = new BigDecimal(0);
//                boolean isExpense = false;
//                if (dataSnapshot.child("description").getValue() == null
//                        || dataSnapshot.child("amount").getValue() == null
//                        || dataSnapshot.child("isExpense").getValue() == null) {
//                    return;
//                }
//                description = dataSnapshot.child("description").getValue(String.class);
//                amount = new BigDecimal(dataSnapshot.child("amount").getValue(String.class));
//                Object object = dataSnapshot.child("isExpense").getValue();
//                if (object != null) {
//                    if (object.toString().equals("true")) {
//                        balance = balance.subtract(amount);
//                        strBalance = "$ " + String.valueOf(balance.setScale(2, RoundingMode
//                                .HALF_UP));
//
//                        isExpense = true;
//                    } else if (object.toString().equals("false")) {
//                        balance = balance.add(amount);
//                        strBalance = "$ " + String.valueOf(balance.setScale(2, RoundingMode
//                                .HALF_UP));
//
//                        isExpense = false;
//                    }
//                }
//            }
//
//            @Override
//            public void onChildChanged(DataSnapshot dataSnapshot, String s) {
//
//            }
//
//            @Override
//            public void onChildRemoved(DataSnapshot dataSnapshot) {
//
//            }
//
//            @Override
//            public void onChildMoved(DataSnapshot dataSnapshot, String s) {
//
//            }
//
//            @Override
//            public void onCancelled(DatabaseError databaseError) {
//
//            }
//        })
//    }
}
