package lk.ac.mrt.cse.dbs.simpleexpensemanager.data.impl;

import android.content.ContentValues;
import android.database.Cursor;

import java.util.ArrayList;
import java.util.List;

import lk.ac.mrt.cse.dbs.simpleexpensemanager.data.AccountDAO;
import lk.ac.mrt.cse.dbs.simpleexpensemanager.data.DataBase;
import lk.ac.mrt.cse.dbs.simpleexpensemanager.data.exception.InvalidAccountException;
import lk.ac.mrt.cse.dbs.simpleexpensemanager.data.model.Account;
import lk.ac.mrt.cse.dbs.simpleexpensemanager.data.model.ExpenseType;

public class PersistentAccount implements AccountDAO {
    private DataBase myDataBase;
    private static final String TABLE_ACCOUNT = "Account";
    private  static  final String ACCOUNT_NO ="accountNo";
    private  static  final String BankName ="bankName";
    private  static  final String AccountHolderName ="accountHolderName";
    private  static  final String Balance ="balance";

    public PersistentAccount(DataBase myDataBase){
        this.myDataBase = myDataBase;
    }


    @Override
    public List<String> getAccountNumbersList() { // get a list of all the account numbers
        List<String> accountNumbersList = new ArrayList<String>();
        Cursor cursor =myDataBase.fetchData(TABLE_ACCOUNT,ACCOUNT_NO,null,null);
        while (cursor.moveToNext()) {
            accountNumbersList.add(cursor.getString(0));
        }
        cursor.close();
        return accountNumbersList;
    }

    @Override
    public List<Account> getAccountsList() { // get all the accounts saved in the database
        List<Account> accountsList = new ArrayList<Account>();
        //select * from Account;
        Cursor cursor =myDataBase.fetchData(TABLE_ACCOUNT,"*",null,null);
        while (cursor.moveToNext()) {
            Account account = new Account(cursor.getString(0), cursor.getString(1), cursor.getString(2), cursor.getDouble(3));
            accountsList.add(account);
        }
        return accountsList;
    }

    @Override
    public Account getAccount(String accountNo) throws InvalidAccountException {
        Account account = null;
        try {
            Cursor cursor = myDataBase.fetchData(TABLE_ACCOUNT, "*", accountNo, ACCOUNT_NO);
            //account = new Account(cursor.getString(0), cursor.getString(1), cursor.getString(2), cursor.getDouble(3));
            account = myDataBase.fetchAccountData(accountNo);
        }catch (Exception e){
            throw new InvalidAccountException("InvalidAccountException");
        }

        return account;

    }

    @Override
    public void addAccount(Account account) { //insert a new account to the database
        ContentValues contentValues =  new ContentValues();
        contentValues.put(ACCOUNT_NO,account.getAccountNo());
        contentValues.put(BankName,account.getBankName());
        contentValues.put(AccountHolderName,account.getAccountHolderName());
        contentValues.put(Balance,account.getBalance());
        this.myDataBase.insertData(TABLE_ACCOUNT,contentValues);

    }

    @Override
    public void removeAccount(String accountNo) throws InvalidAccountException {
        int noOfRowsDeleted = this.myDataBase.deleteData("Account","accountNo",accountNo);
        if(noOfRowsDeleted == 0){
            throw new InvalidAccountException("InvalidAccountException");
        }

    }

    @Override
    public void updateBalance(String accountNo, ExpenseType expenseType, double amount) throws InvalidAccountException {
        double balance=0.0;
        double total=0.0;
        try{
            //Account account = getAccount(accountNo);
            Account account =myDataBase.fetchAccountData(accountNo);
            balance = account.getBalance();
        }catch (Exception e){
            throw new InvalidAccountException("InvalidAccountException");
        }
        if (expenseType == ExpenseType.EXPENSE){
            if(balance < amount){
                throw new InvalidAccountException("negative Account Balance");
            }
            total = balance-amount;
        }else if (expenseType == ExpenseType.INCOME) {
            total = amount +balance;
        }else{
            throw new InvalidAccountException("Invalid expenseType");
        }
        ContentValues ContentValues = new ContentValues();
        ContentValues.put(Balance, total);
        //boolean flag = this.myDataBase.updateData("account",ContentValues,condition);
        boolean flag = this.myDataBase.updateBalanceData(TABLE_ACCOUNT,ContentValues,ACCOUNT_NO,accountNo);
        if(flag){
            System.out.println("Updated successfully");
        }else {
            throw new InvalidAccountException("InvalidAccountException");
        }

    }
    
}
