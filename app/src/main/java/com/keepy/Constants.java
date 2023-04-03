package com.keepy;

import android.annotation.SuppressLint;
import android.app.TimePickerDialog;
import android.content.Context;
import android.graphics.Color;
import android.graphics.PorterDuff;
import android.graphics.Typeface;
import android.view.Gravity;
import android.view.MotionEvent;
import android.view.View;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TableLayout;
import android.widget.TableRow;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AlertDialog;
import androidx.core.content.ContextCompat;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.google.android.gms.tasks.OnSuccessListener;
import com.keepy.adapters.RequestsRvAdapter;
import com.keepy.behaviour.IKeeperProfile;
import com.keepy.behaviour.IRequests;
import com.keepy.models.ServiceRequest;
import com.keepy.models.User;

import org.w3c.dom.Text;

import java.text.DateFormatSymbols;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.List;

import javax.annotation.Nullable;


public class Constants {


    public static final String PrefLang = "English";

    public static final String AppName = "Keepy";
    public static final String DogisterType = "dogister";
    public static final String TherapistType = "therapist";
    public static final String BabySitterType = "babysitter";
    public static final String HouseKeeperType = "housekeeper";
    public static final String BabySitterDisabilitiesType = "babysitter_disabilities";

    public static final String DogisterType_View = "Dogister";
    public static final String TherapistType_View = "Therapist";
    public static final String BabySitterType_View = "Babysitter";
    public static final String HouseKeeperType_View = "House keeper";
    public static final String BabySitterDisabilitiesType_View = "Babysitter for children with disabilities";


    @SuppressLint("SimpleDateFormat")
    public static String getDateStringFromTimeMilies(long timeMillies) {
        return new java.text.SimpleDateFormat("dd/MM/yyyy HH:mm")
                .format(new java.util.Date(timeMillies));
    }

    public static String[] places = {"Tel aviv", "Jerusalem", "Ramat gan", "Givatayim", "Bnei brak", "Haifa", "Ashdod", "Ashkelon",
            "Ramat hasharon", "Rishone lezion", "Bat yam", "Holon", "Netanya", "Raanana", "Hadera", "Binyamina", "Zichron"
            , "Rehovot", "Nes ziona", "Eilat", "Petah tiqwa", "Beer sheva", "Afula", "Akko", "Arad", "Beit shemesh", "Elad"
            , "Herzliya", "Kfar sava", "Lod", "Ramla", "Netivot", "Nof hagalil", "Or yehuda", "Yehud", "Yavne"};

    public static final String UsersCollection = "Users";
    public static final String UsersCollectionEmailField = "mEmail";

    public static final String ServiceRequestsCollection_outgoing = "outgoingRequests";
    public static final String ServiceRequestsCollection_incoming = "incomingRequests";

    public static final String ServiceRequestsCollection_outgoing_clientIdField = "clientId";
    public static final String ServiceRequestsCollection_outgoing_keeperIdField = "keeperId";
    public static final String ServiceRequestsCollection_outgoing_dateField = "date";
    public static final String ServiceRequestsCollection_outgoing_requestDateField = "requestDate";
    public static final String ServiceRequestsCollection_outgoing_clientCommentField = "clientComment";

    public static final String ServiceRequestsCollection_incoming_clientIdField = "clientId";
    public static final String ServiceRequestsCollection_incoming_keeperIdField = "keeperId";
    public static final String ServiceRequestsCollection_incoming_dateField = "date";
    public static final String ServiceRequestsCollection_incoming_requestDateField = "requestDate";


    public static class Utils {

        public static String getRequestStatusString(int status) {
            switch (status) {
                case ServiceRequest.Status.APPROVED:
                    return "Approved";
                case ServiceRequest.Status.DECLINED:
                    return "Declined";
                case ServiceRequest.Status.WAITING:
                    return "Waiting Approval";
                case ServiceRequest.Status.FULFILLED:
                    return "Fulfilled";
                default:
                    return "Unknown";
            }
        }

        public static boolean isIsraeliPhoneNumber(String str) {
            if (str.length() != 10)
                return false;

            if (str.charAt(0) != '0')
                return false;

            if (str.charAt(1) != '5')
                return false;
            for (int i = 2; i < str.length(); i++) {
                if (!Character.isDigit(str.charAt(i))) {
                    return false;
                }
            }
            return true;
        }

        public static ServiceTime getDefaultServiceTime() {
            return new ServiceTime(0, 0, 0, 0, 0);
        }


        /*
            Returns a 2D array of integers representing the calendar data for the given month.
            The first dimension of the array represents the week of the month, and the second
            dimension represents the day of the week. The value of each element is the day of
            the month, or 0 if the day is not part of the given month.
         */
        public int[][] getCalendarData(int year, int month) {
            Calendar cal = Calendar.getInstance();
            cal.set(year, month - 1, 1); // set the calendar to the first day of the month

            int numDaysInMonth = cal.getActualMaximum(Calendar.DAY_OF_MONTH);
            int firstDayOfWeek = cal.get(Calendar.DAY_OF_WEEK);

            int[][] calendarData = new int[6][7]; // assuming no month has more than 6 weeks

            int day = 1;
            for (int week = 0; week < 6; week++) {
                for (int weekday = 0; weekday < 7; weekday++) {
                    if (week == 0 && weekday < firstDayOfWeek - 1) {
                        // fill in the days from the previous month in the first week
                        calendarData[week][weekday] = 0;
                    } else if (day > numDaysInMonth) {
                        // fill in the remaining days with 0
                        calendarData[week][weekday] = 0;
                    } else {
                        // fill in the current day of the month
                        calendarData[week][weekday] = day;
                        day++;
                    }
                }
            }

            return calendarData;
        }


        final static String[] hasSelectedDate = {""};
        final static String[] hasSelectedTime = {""};

        public static void clearSelectedDate() {
            hasSelectedDate[0] = null;
            hasSelectedTime[0] = null;
        }


        public static AlertDialog tempDialogPtr;

        /*
              a Calendar UI for the given month and year and adds it to the given layout.
         */
        public static void calendarUI(
                // Array of selected dates
                List<ServiceRequest> serviceRequestList,
                boolean scheduleAsClient,
                boolean clientView,
                @Nullable User client,
                @Nullable User keeper,
                @Nullable IKeeperProfile keeperProfile,
                @Nullable IRequests requestsCallback,
                @Nullable OnSuccessListener<ServiceTime> onSuccessRequest,
                Context activity,
                TableLayout calendarLayout,
                int year, int month, String language) {
            calendarLayout.removeAllViews();
            int[][] calendarData = new Utils().getCalendarData(year, month);
            Calendar cal = Calendar.getInstance();
            int today = cal.get(Calendar.DAY_OF_MONTH);
            TextView monthYearTV = new TextView(activity);
            int orangeColor = Color.parseColor("#FFA000");

            monthYearTV.setLayoutParams(new TableRow.LayoutParams(TableRow.LayoutParams.WRAP_CONTENT, TableRow.LayoutParams.WRAP_CONTENT));
            monthYearTV.setGravity(Gravity.CENTER);
            monthYearTV.setPadding(24, 24, 24, 24);
            String monthName = new DateFormatSymbols().getMonths()[month - 1];
            monthYearTV.setText(monthName + "  " + year);
            monthYearTV.setBackgroundColor(orangeColor);
            monthYearTV.setTextColor(Color.WHITE);
            monthYearTV.setTextSize(24);
            monthYearTV.setTypeface(null, Typeface.BOLD);
            // linear layout for buttons
            LinearLayout buttonsLayout = new LinearLayout(activity);
            buttonsLayout.setLayoutParams(new TableRow.LayoutParams(TableRow.LayoutParams.WRAP_CONTENT, TableRow.LayoutParams.WRAP_CONTENT));
            buttonsLayout.setOrientation(LinearLayout.HORIZONTAL);
            buttonsLayout.setGravity(Gravity.CENTER);
            buttonsLayout.setPadding(24, 24, 24, 24);
            // previous month button
            ImageView prevMonthButton = new ImageView(activity);
            prevMonthButton.setLayoutParams(new TableRow.LayoutParams(100, 100));
            prevMonthButton.setImageResource(R.drawable.arrowleft);
            prevMonthButton.setOnClickListener(v -> {
                // update the calendar UI
                if (month == 1)
                    calendarUI(serviceRequestList, scheduleAsClient, clientView, client, keeper, keeperProfile, requestsCallback, onSuccessRequest, activity, calendarLayout, year - 1, 12, language);
                else
                    calendarUI(serviceRequestList, scheduleAsClient, clientView, client, keeper, keeperProfile, requestsCallback, onSuccessRequest, activity, calendarLayout, year, month - 1, language);
            });
            buttonsLayout.addView(prevMonthButton);
            // spacer
            View spacer = new View(activity);
            spacer.setLayoutParams(new TableRow.LayoutParams(0, 0, 1));
            buttonsLayout.addView(spacer);
            // next month button
            ImageView nextMonthButton = new ImageView(activity);
            nextMonthButton.setLayoutParams(new TableRow.LayoutParams(100, 100));
            nextMonthButton.setImageResource(R.drawable.arrowright);
            nextMonthButton.setOnClickListener(v -> {
                // update the calendar UI
                if (month == 12)
                    calendarUI(serviceRequestList, scheduleAsClient, clientView, client, keeper, keeperProfile, requestsCallback, onSuccessRequest, activity, calendarLayout, year + 1, 1, language);
                else
                    calendarUI(serviceRequestList, scheduleAsClient, clientView, client, keeper, keeperProfile, requestsCallback, onSuccessRequest, activity, calendarLayout, year, month + 1, language);
            });
            buttonsLayout.addView(nextMonthButton);
            // add the buttons layout to the calendar layout
            calendarLayout.addView(buttonsLayout);
            calendarLayout.addView(monthYearTV);


            // create the header row
            TableRow headerRow = new TableRow(activity);
            headerRow.setLayoutParams(new TableRow.LayoutParams(TableRow.LayoutParams.MATCH_PARENT, TableRow.LayoutParams.WRAP_CONTENT));
            headerRow.setBackgroundColor(Color.parseColor("#FFC107"));
            String[] weekdays;
            if (language.equals("English"))
                weekdays = new DateFormatSymbols().getShortWeekdays();
            else
                weekdays = new String[]{"", "א", "ב", "ג", "ד", "ה", "ו", "ש"};
            for (int i = 0; i <= 7; i++) {
                TextView tv = new TextView(activity);
                tv.setLayoutParams(new TableRow.LayoutParams(TableRow.LayoutParams.WRAP_CONTENT, TableRow.LayoutParams.WRAP_CONTENT));
                tv.setGravity(Gravity.CENTER);
                tv.setWidth(100);
                tv.setHeight(100);
                tv.setText(weekdays[i]);
                tv.setTextColor(Color.WHITE);
                // translate tv to the left to match the calendar with saturday
                tv.setTranslationX(-120);
                headerRow.addView(tv);
            }

            calendarLayout.addView(headerRow);
            calendarLayout.setStretchAllColumns(true);
            // create the rest of the rows
            for (int week = 0; week < 6; week++) {
                TableRow row = new TableRow(activity);
                // set table gaps
                row.setLayoutParams(new TableRow.LayoutParams(TableRow.LayoutParams.MATCH_PARENT, TableRow.LayoutParams.WRAP_CONTENT));
                for (int weekday = 0; weekday < 7; weekday++) {
                    if (calendarData[week][weekday] == 0) {
                        // if the day is not part of the current month, just add an empty cell
                        TextView tv = new TextView(activity);
                        tv.setLayoutParams(new TableRow.LayoutParams(TableRow.LayoutParams.WRAP_CONTENT, TableRow.LayoutParams.WRAP_CONTENT));
                        tv.setGravity(Gravity.CENTER);
                        tv.setPadding(5, 5, 5, 5);
                        tv.setText("");
                        tv.setTextColor(Color.BLACK);
                        // if in client view make dates clickable
                        row.addView(tv);
                        continue;
                    }

                    TextView tv = new TextView(activity);

                    tv.setTextColor(Color.BLACK);
                    if (today == calendarData[week][weekday] && month == cal.get(Calendar.MONTH) + 1 && year == cal.get(Calendar.YEAR)) {
                        tv.setTextColor(Color.WHITE);
                        tv.setBackground(ContextCompat.getDrawable(activity, R.drawable.circle_month_today));
                    }

                    tv.setWidth(100);
                    tv.setHeight(100);
                    TableRow.LayoutParams params = new TableRow.LayoutParams(TableRow.LayoutParams.WRAP_CONTENT, TableRow.LayoutParams.WRAP_CONTENT);
                    params.setMargins(12, 12, 12, 12);
                    tv.setLayoutParams(params);
                    // color the selected dates
                    if (!scheduleAsClient && serviceRequestList != null && !serviceRequestList.isEmpty()) {
                        for (ServiceRequest selectedDate : serviceRequestList) {
                            if (selectedDate.getServiceTime().getDay() == calendarData[week][weekday]
                                    && selectedDate.getServiceTime().getMonth() == month
                                    && selectedDate.getServiceTime().getYear() == year) {
                                tv.setBackground(ContextCompat.getDrawable(activity, R.drawable.circle_month));
                                tv.setTextColor(Color.WHITE);
                                addRippleEffect(tv);
                                tv.setOnClickListener(v -> {
                                    AlertDialog.Builder builder = new AlertDialog.Builder(activity);
                                    if (clientView) {
                                        builder.setTitle("My Requests");
                                    } else {
                                        builder.setTitle("Service Requests");
                                    }
                                    ArrayList<ServiceRequest> dateRequests = filterOnDate(serviceRequestList, selectedDate);
                                    builder.setMessage("You have " + dateRequests.size() + " services scheduled on this day");
                                    builder.setPositiveButton("OK", (dialog, which) -> dialog.dismiss());
                                    builder.setMessage("You have " + dateRequests.size() + " services scheduled on this day");
                                    builder.setPositiveButton("OK", (dialog, which) -> dialog.dismiss());
                                    builder.setView(
                                            createServiceRequestListView(
                                                    dateRequests,
                                                    activity,
                                                    selectedDate,
                                                    requestsCallback,
                                                    client,
                                                    clientView,
                                                    keeper,
                                                    keeperProfile,
                                                    onSuccessRequest,
                                                    language,
                                                    builder
                                            )
                                    );
                                    tempDialogPtr = builder.show();
                                    builder.setOnDismissListener(dialog -> tempDialogPtr = null);
                                    builder.setOnCancelListener(dialog -> tempDialogPtr = null);
                                });
                                break;
                            }
                        }
                    } else {
                        final int finalWeek = week;
                        final int finalWeekday = weekday;
                        tv.setOnLongClickListener(v1 -> {
                            if (hasSelectedDate[0] == null)
                                return false;
                            if (!hasSelectedDate[0].equals(
                                    calendarData[finalWeek][finalWeekday] + "/" + month + "/" + year)
                            ) {
                                Toast.makeText(activity,
                                        "You can only cancel the selected date",
                                        Toast.LENGTH_SHORT).show();
                                return false;
                            }
                            tv.setBackground(null);
                            tv.setTextColor(Color.BLACK);
                            clearSelectedDate();
                            Toast.makeText(activity, "Date unselected", Toast.LENGTH_SHORT).show();
                            return true;
                        });
                        if (scheduleAsClient)
                            tv.setOnClickListener(v -> {
                                // if the date is not in the past
                                if (year > cal.get(Calendar.YEAR) || (year == cal.get(Calendar.YEAR) && month > cal.get(Calendar.MONTH) + 1) || (year == cal.get(Calendar.YEAR) && month == cal.get(Calendar.MONTH) + 1 && calendarData[finalWeek][finalWeekday] >= today)) {
                                    // if the date is not already selected
                                    if (!isDateSelected(serviceRequestList, calendarData[finalWeek][finalWeekday], month, year)) {
                                        if (hasSelectedDate[0] != null) {
                                            if (hasSelectedDate[0].equals(
                                                    calendarData[finalWeek][finalWeekday] + "/" + month + "/" + year)) {
                                                Toast.makeText(activity, "You have already selected this date at " +
                                                        hasSelectedTime[0] +
                                                        ", press and hold to cancel", Toast.LENGTH_SHORT).show();
                                            } else {
                                                Toast.makeText(activity, "You can only select one date, press and hold selected blue date to cancel", Toast.LENGTH_SHORT).show();
                                            }
                                            return;
                                        }

                                        openTimePicker(serviceRequestList,
                                                activity,
                                                calendarLayout,
                                                client,
                                                keeper,
                                                keeperProfile,
                                                onSuccessRequest,
                                                year,
                                                month,
                                                calendarData[finalWeek][finalWeekday],
                                                language,
                                                unused -> {
                                                    hasSelectedDate[0] = calendarData[finalWeek][finalWeekday] + "/" + month + "/" + year;
                                                    TextView tvt = (TextView) v;
                                                    tvt.setBackground(ContextCompat.getDrawable(activity, R.drawable.circle_month));
                                                    tvt.setTextColor(Color.WHITE);
                                                });
                                    }
                                } else {
                                    Toast.makeText(activity, "Date selected is in the past", Toast.LENGTH_SHORT).show();
                                }
                            });
                    }


                    tv.setGravity(Gravity.CENTER);
                    tv.setPadding(5, 5, 5, 5);
                    tv.setText(String.valueOf(calendarData[week][weekday]));
                    row.addView(tv);
                }

                calendarLayout.addView(row);
            }
            calendarLayout.setPadding(30, 30, 30, 30);
            if (!clientView)
                calendarLayout.addView(getColorIndicators(activity, language, scheduleAsClient, calendarLayout, client, keeper, requestsCallback, keeperProfile, serviceRequestList));
        }

        private static ArrayList<ServiceRequest> filterOnDate(List<ServiceRequest> serviceRequestList, ServiceRequest selectedDate) {
            ArrayList<ServiceRequest> filterOnDate = new ArrayList<>();
            for (ServiceRequest request : serviceRequestList) {
                if (selectedDate.getServiceTime()
                        .sameDate(request.getServiceTime()))
                    filterOnDate.add(request);
            }

            return filterOnDate;
        }

        private static View createServiceRequestListView(List<ServiceRequest> serviceRequestList,
                                                         Context activity,
                                                         ServiceRequest selectedDate,
                                                         IRequests requestsCallback,
                                                         User client,
                                                         boolean clientView,
                                                         User keeper,
                                                         IKeeperProfile keeperProfile,
                                                         OnSuccessListener<ServiceTime> onSuccessRequest,
                                                         String language,
                                                         AlertDialog.Builder d) {

            LinearLayout linearLayout = new LinearLayout(activity);
            linearLayout.setOrientation(LinearLayout.VERTICAL);
            linearLayout.setPadding(30, 30, 30, 30);
            linearLayout.setGravity(Gravity.CENTER);
            linearLayout.setLayoutParams(new LinearLayout.LayoutParams(LinearLayout.LayoutParams.MATCH_PARENT, LinearLayout.LayoutParams.MATCH_PARENT));

            // create the rv
            RecyclerView recyclerView = new RecyclerView(activity);
            recyclerView.setLayoutParams(new LinearLayout.LayoutParams(LinearLayout.LayoutParams.MATCH_PARENT, LinearLayout.LayoutParams.MATCH_PARENT));
            recyclerView.setHasFixedSize(true);
            recyclerView.setLayoutManager(new LinearLayoutManager(activity));
            recyclerView
                    .setAdapter(
                            new RequestsRvAdapter(
                                    serviceRequestList,
                                    requestsCallback,
                                    clientView,
                                    false,
                                    false,
                                    false));
            d.setView(recyclerView);
            return recyclerView;
        }

        public static List<ServiceTime> getDefaultAllDayServiceTimes() {
            List<ServiceTime> serviceTimes = new ArrayList<>();
            for (int i = 0; i < 24; i++)
                serviceTimes.add(new ServiceTime(0, i, 0, i + 1, 0));
            return serviceTimes;
        }

        private static void openTimePicker(List<ServiceRequest> serviceRequestList,
                                           Context activity,
                                           TableLayout calendarLayout,
                                           User client,
                                           User keeper,
                                           IKeeperProfile keeperProfile,
                                           OnSuccessListener<ServiceTime> onSuccess,
                                           int year,
                                           int month,
                                           int i,
                                           String language,
                                           OnSuccessListener<Void> onSelected) {
            TimePickerDialog timePickerDialog = new TimePickerDialog(activity, (view, hourOfDay, minute) -> {
                Calendar calendar = Calendar.getInstance();
                calendar.set(year, month, i, hourOfDay, minute);

                if (isValidKeeperTime(keeper, serviceRequestList, hourOfDay, minute)) {
                    ServiceTime serviceTime = new ServiceTime(calendar.getTimeInMillis(), hourOfDay, minute, hourOfDay + 1, minute);
                    onSuccess.onSuccess(serviceTime);
                    hasSelectedTime[0] = getTimeString(serviceTime.getStartHour()) + " - " + getTimeString(serviceTime.getEndHour());
                    Toast.makeText(activity, "Selected " +
                            i + "/" + month + "/" + year +
                            " at " + hasSelectedTime[0], Toast.LENGTH_SHORT).show();
                    onSelected.onSuccess(null);
                } else {
                    Toast.makeText(activity, "The keeper is not available at this time", Toast.LENGTH_SHORT).show();
                }
            }, 0, 0, true);
            timePickerDialog.show();
        }

        private static boolean isValidKeeperTime(User keeper,
                                                 List<ServiceRequest> existingRequests,
                                                 int startHour,
                                                 int startMinute) {


            // return true if the keeper does services on requested date and there are no overlapping services
            if (keeper.getmKeeperData().servesTime(startHour, startMinute)) {
                for (ServiceRequest request : existingRequests) {
                    if (isBetween(request.getServiceTime(), startHour)) {
                        return false;
                    }
                }
                return true;
            }
            return false;
        }

        private static String getTimeString(int time) {
            if (time < 10)
                return "0" + time;
            return String.valueOf(time);
        }

        private static boolean isBetween(ServiceTime time,
                                         int startHour) {
            return time.getStartHour() <= startHour && time.getEndHour() >= startHour + 1;
        }

        private static boolean isDateSelected(List<ServiceRequest> serviceRequestList, int i, int month, int year) {
            for (ServiceRequest selectedDate : serviceRequestList) {
                int[] monthYearForRequestService = getDayMonthYearForRequestService(selectedDate);
                if (monthYearForRequestService[0] == i
                        && monthYearForRequestService[1] == month
                        && monthYearForRequestService[2] == year) {
                    return true;
                }
            }
            return false;
        }

        private static LinearLayout getColorIndicators(
                Context activity,
                String language,
                boolean scheduleAsClient,
                TableLayout calendarLayout,
                User client,
                User keeper,
                IRequests requestsCallback,
                IKeeperProfile keeperProfile,
                List<ServiceRequest> serviceRequestList
        ) {
            // color indicators header layout
            LinearLayout colorIndicatorsLayout = new LinearLayout(activity);
            colorIndicatorsLayout.setWeightSum(2);
            colorIndicatorsLayout.setLayoutParams(new TableRow.LayoutParams(TableRow.LayoutParams.WRAP_CONTENT, TableRow.LayoutParams.WRAP_CONTENT));
            colorIndicatorsLayout.setOrientation(LinearLayout.HORIZONTAL);
            colorIndicatorsLayout.setGravity(Gravity.CENTER);

            // color indicators
            TextView colorIndicator1 = new TextView(activity);
            // set weight to 2
            colorIndicator1.setLayoutParams(new TableRow.LayoutParams(TableRow.LayoutParams.WRAP_CONTENT, TableRow.LayoutParams.WRAP_CONTENT, 1));
            colorIndicator1.setGravity(Gravity.CENTER);
            colorIndicator1.setTypeface(null, Typeface.BOLD);
            colorIndicator1.setTextSize(14);

            colorIndicator1.setPadding(24, 40, 24, 40);
            colorIndicator1.setText(
                    language.equals("English") ? "Closest meetings to today" : "פגישות הקרובות ביותר להיום"
            );
            colorIndicator1.setOnClickListener(v -> {
                //show the closest date to today in which the user has service requests in
                // by updating the calendarUI
                Calendar cal = Calendar.getInstance();
                int year = cal.get(Calendar.YEAR);
                int month = cal.get(Calendar.MONTH) + 1;
                int day = cal.get(Calendar.DAY_OF_MONTH);
                int[] closestDate = getClosestDate(serviceRequestList, day, month, year);
                calendarUI(serviceRequestList, scheduleAsClient, false, client, keeper, keeperProfile, requestsCallback, null, activity, calendarLayout, closestDate[2], closestDate[1], language);
            });
            addRippleEffect(colorIndicator1);
            colorIndicator1.setTextColor(Color.WHITE);
            colorIndicator1.setBackgroundColor(Color.parseColor("#3F51B5"));
            colorIndicatorsLayout.addView(colorIndicator1);
            TextView colorIndicator2 = new TextView(activity);
            colorIndicator2.setLayoutParams(new TableRow.LayoutParams(TableRow.LayoutParams.WRAP_CONTENT, TableRow.LayoutParams.WRAP_CONTENT, 1));
            colorIndicator2.setGravity(Gravity.CENTER);
            colorIndicator2.setTypeface(null, Typeface.BOLD);
            colorIndicator2.setTextSize(14);
            colorIndicator2.setPadding(24, 40, 24, 40);
            colorIndicator2.setText(
                    language.equals("English") ? "Move to today" : "עבור להיום"
            );
            colorIndicator2.setOnClickListener(v -> {
                // change calendar today date with calendarUI
                Calendar cal = Calendar.getInstance();
                int year = cal.get(Calendar.YEAR);
                int month = cal.get(Calendar.MONTH) + 1;
                calendarUI(serviceRequestList, scheduleAsClient, false, client, keeper, keeperProfile, requestsCallback, null, activity, calendarLayout, year, month, language);
            });
            addRippleEffect(colorIndicator2);

            colorIndicator2.setTextColor(Color.WHITE);
            colorIndicator2.setBackgroundColor(Color.parseColor("#4CAF50"));
            colorIndicatorsLayout.addView(colorIndicator2);
            return colorIndicatorsLayout;
        }

        private static int[] getClosestDate(List<ServiceRequest> serviceRequestList, int day, int month, int year) {
            int[] closestDate = new int[3];
            closestDate[0] = 100;
            closestDate[1] = month;
            closestDate[2] = year;
            for (ServiceRequest serviceRequest : serviceRequestList) {
                int[] date = getDayMonthYearForRequestService(serviceRequest);
                if (date[2] == year && date[1] == month) {
                    if (Math.abs(date[0] - day) < closestDate[0]) {
                        closestDate[0] = Math.abs(date[0] - day);
                        closestDate[1] = date[1];
                        closestDate[2] = date[2];
                    }
                }
            }
            return closestDate;
        }

        public static int getDayForRequestService(ServiceRequest serviceRequest) {
            long dateTime = serviceRequest.getDate();
            Calendar calendar = Calendar.getInstance();
            calendar.setTimeInMillis(dateTime);
            return calendar.get(Calendar.DAY_OF_MONTH);
        }

        public static int[] getDayMonthYearForRequestService(ServiceRequest serviceRequest) {
            long dateTime = serviceRequest.getDate();
            Calendar calendar = Calendar.getInstance();
            calendar.setTimeInMillis(dateTime);
            int[] date = new int[3];
            date[0] = calendar.get(Calendar.DAY_OF_MONTH);
            date[1] = calendar.get(Calendar.MONTH) + 1;
            date[2] = calendar.get(Calendar.YEAR);
            return date;
        }


        @SuppressLint("ClickableViewAccessibility")
        public static void addRippleEffect(View view) {
            view.setOnTouchListener((v, event) -> {
                switch (event.getAction()) {
                    case MotionEvent.ACTION_DOWN: {
                        view.getBackground().setColorFilter(0xe0f47521, PorterDuff.Mode.SRC_ATOP);
                        view.invalidate();
                        break;
                    }
                    case MotionEvent.ACTION_UP:
                    case MotionEvent.ACTION_CANCEL: {
                        //clear the overlay
                        view.getBackground().clearColorFilter();
                        view.invalidate();
                        break;
                    }
                }
                return false;
            });
        }

        public static String getKeeperNameAsAppropriateString(String keeperType) {
            switch (keeperType) {
                case DogisterType:
                    return DogisterType_View;
                case TherapistType:
                    return TherapistType_View;
                case BabySitterType:
                    return BabySitterType_View;
                case BabySitterDisabilitiesType:
                    return BabySitterDisabilitiesType_View;
                case HouseKeeperType:
                    return HouseKeeperType_View;
                default:
                    return "Keeper";
            }
        }

        public static String getKeeperTypeFromAppropriateString(String keeperName) {
            switch (keeperName) {
                case DogisterType_View:
                    return DogisterType;
                case TherapistType_View:
                    return TherapistType;
                case BabySitterType_View:
                    return BabySitterType;
                case BabySitterDisabilitiesType:
                    return BabySitterDisabilitiesType_View;
                case HouseKeeperType_View:
                    return HouseKeeperType;
                default:
                    return "keeper";
            }
        }
    }




    /*
        Rating recommendation min requirement & status field & colors
     */

    public static final float RatingRecommendationMinRequirement = 4.5f;

    public static final String ServiceRequestsCollection_statusField = "status";

    public static final int ColorSelected = Color.parseColor("#5793a8");
    public static final int ColorUnselected = Color.parseColor("#8692f7");

}
