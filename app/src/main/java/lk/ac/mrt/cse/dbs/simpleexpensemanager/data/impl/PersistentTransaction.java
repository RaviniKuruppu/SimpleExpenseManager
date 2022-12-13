package lk.ac.mrt.cse.dbs.simpleexpensemanager.data.impl;

import android.content.ContentValues;
import android.database.Cursor;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Locale;

import lk.ac.mrt.cse.dbs.simpleexpensemanager.data.DataBase;
import lk.ac.mrt.cse.dbs.simpleexpensemanager.data.TransactionDAO;
import lk.ac.mrt.cse.dbs.simpleexpensemanager.data.exception.InvalidAccountException;
import lk.ac.mrt.cse.dbs.simpleexpensemanager.data.model.Account;
import lk.ac.mrt.cse.dbs.simpleexpensemanager.data.model.ExpenseType;
import lk.ac.mrt.cse.dbs.simpleexpensemanager.data.model.Transaction;

public class PersistentTransaction implements TransactionDAO {
    private DataBase myDataBase;
    private static final String TABLE_TRANSACTION = "Transactions";
    private  static  final String DATE ="date";
    private  static  final String ACCOUNT_NO ="accountNo";
    private  static  final String EXPENSE_TYPE ="expenseType";
    private  static  final String AMOUNT ="amount";

    public PersistentTransaction(DataBase myDataBase){
        this.myDataBase = myDataBase;
    }

    @Override
    public void logTransaction(Date date, String accountNo, ExpenseType expenseType, double amount) { // add a transaction in to the database

        double balance=0.0;
        try{
            PersistentAccount persistentAccount = new PersistentAccount(this.myDataBase);
            Account account =persistentAccount.getAccount(accountNo);
            balance = account.getBalance();

        }catch (Exception e){
            System.out.println("InvalidAccountException");
        }

        if (balance<amount){
            System.out.println("Insufficient Account Balance");
        }else{
        ContentValues contentValues =  new ContentValues();
        contentValues.put(DATE,date.toString());
        contentValues.put(ACCOUNT_NO,accountNo);
        // convert expense to string
        String expense;
        if(expenseType == ExpenseType.EXPENSE){
            expense="EXPENSE";
        }else{
            expense="INCOME";
        }
        contentValues.put(EXPENSE_TYPE,expense );
        contentValues.put(AMOUNT,amount);
        this.myDataBase.insertData(TABLE_TRANSACTION,contentValues); //insert data into the database
        }

    }

    @Override
    public List<Transaction> getAllTransactionLogs() { //get all the transactions in the database
        List<Transaction> allTransactionLogs = new ArrayList<Transaction>();
        Cursor cursor =myDataBase.fetchData(TABLE_TRANSACTION,"*",null,null);
        while (cursor.moveToNext()) {
            String date = cursor.getString(cursor.getColumnIndex(DATE));
            String accountNo = cursor.getString(cursor.getColumnIndex(ACCOUNT_NO));
            String expenseType = cursor.getString(cursor.getColumnIndex(EXPENSE_TYPE));
            double amount = cursor.getDouble(cursor.getColumnIndex(AMOUNT));

            //convert string to ExpenseType
            ExpenseType expenseTypeClass;
            if (expenseType.equals("EXPENSE")) {
                expenseTypeClass = ExpenseType.EXPENSE;
            } else {
                expenseTypeClass = ExpenseType.INCOME;
            }
            //convert string to date
            //Date date1 = null;
            SimpleDateFormat dateFormat = new SimpleDateFormat("EEE MMM dd HH:mm:ss zzz yyyy", Locale.ENGLISH);
            Date date1 = new Date();
            try {
                date1 = dateFormat.parse(date);
            } catch (Exception e) {
                System.out.println(e);
            }

            Transaction transaction = new Transaction(date1, accountNo, expenseTypeClass, amount);
            allTransactionLogs.add(transaction); //add all the transactions in to the list
        }
        cursor.close();
        return allTransactionLogs;

    }

    @Override
    public List<Transaction> getPaginatedTransactionLogs(int limit) {
        List<Transaction> getPaginatedTransactionLogs = new ArrayList<Transaction>();
        Cursor cursor =myDataBase.fetchDataWithLimit(TABLE_TRANSACTION,"*",null,null,limit);
        while (cursor.moveToNext()) {
            String date = cursor.getString(cursor.getColumnIndex(DATE));
            String accountNo = cursor.getString(cursor.getColumnIndex(ACCOUNT_NO));
            String expenseType = cursor.getString(cursor.getColumnIndex(EXPENSE_TYPE));
            double amount = cursor.getDouble(cursor.getColumnIndex(AMOUNT));

            ExpenseType expenseTypeClass;
            if (expenseType.equals("EXPENSE")) {
                expenseTypeClass = ExpenseType.EXPENSE;
            } else {
                expenseTypeClass = ExpenseType.INCOME;
            }

            SimpleDateFormat dateFormat = new SimpleDateFormat("EEE MMM dd HH:mm:ss zzz yyyy", Locale.ENGLISH);
            Date date1 = new Date();
            try {
                date1 = dateFormat.parse(date);
            } catch (Exception e) {
                System.out.println(e);
            }

            Transaction transaction = new Transaction(date1, accountNo, expenseTypeClass, amount);
            getPaginatedTransactionLogs.add(transaction);
        }
        return getPaginatedTransactionLogs;
    }
}
