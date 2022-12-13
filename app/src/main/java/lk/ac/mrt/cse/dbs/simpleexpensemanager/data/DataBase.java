package lk.ac.mrt.cse.dbs.simpleexpensemanager.data;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.support.annotation.Nullable;

import lk.ac.mrt.cse.dbs.simpleexpensemanager.data.model.Account;

public class DataBase  extends SQLiteOpenHelper {
    private static final String DATABASE_NAME = "200334F.db";
    private static final String TABLE_ACCOUNT = "Account";
    private static final String TABLE_TRANSACTION = "Transactions";
    public DataBase(@Nullable Context context) {
        super(context, DATABASE_NAME, null, 2);
    }

    @Override
    public void onCreate(SQLiteDatabase sqLiteDatabase) { //create table
        sqLiteDatabase.execSQL("create table "+TABLE_ACCOUNT+"(accountNo TEXT PRIMARY KEY ,bankName TEXT ,accountHolderName TEXT ,balance REAL)");
        sqLiteDatabase.execSQL("create table "+TABLE_TRANSACTION+"(Transaction_ID INTEGER  PRIMARY KEY AUTOINCREMENT,date TEXT ,accountNo TEXT ,expenseType TEXT ,amount REAL)");

    }

    @Override
    public void onUpgrade(SQLiteDatabase sqLiteDatabase, int i, int i1) {
        sqLiteDatabase.execSQL("DROP TABLE IF EXISTS "+TABLE_ACCOUNT);
        sqLiteDatabase.execSQL("DROP TABLE IF EXISTS "+TABLE_TRANSACTION);
        onCreate(sqLiteDatabase);
    }


    public boolean insertData(String table, ContentValues contentValues){ // insert data into the database
        long flag = -1;
        try{
            SQLiteDatabase db = this.getWritableDatabase();
            flag= db.insert(table,null,contentValues);
        }catch (Exception e){
            flag = -1;
            System.out.println(e);
        }
            
        if (flag==-1){ //insert function return -1 if there is an error in insertion
            return false;
        }else {
            return true;
        }

    }

    public Integer deleteData(String table_name, String columnName, String id){
        try{
            SQLiteDatabase db = this.getWritableDatabase();
            //delete return 0 when no data is deleted
            return db.delete(table_name, columnName+" = ?", new String[] {id});
        }catch (Exception e){
            System.out.println(e);
            return 0;
        }

    }

    public Cursor fetchData(String table_name, String columnName,String criteria,String criteriaColum){
        try{
            SQLiteDatabase db = this.getReadableDatabase();
            if (criteria==null){
                String sql = "select "+columnName+" from "+table_name;
                Cursor cursor =db.rawQuery(sql, null);
                return cursor;
            }else{
                String sql = "select "+columnName+" from "+table_name+" where "+criteriaColum + " = '" +criteria+"';";
                Cursor cursor1 =db.rawQuery(sql, null);
                System.out.println(sql);
                return cursor1;
            }
        }catch (Exception e){
            System.out.println(e);
            return null;
        }


    }

    public Cursor fetchDataWithLimit(String table_name, String columnName,String criteria,String criteriaColum,int limit){
        try{
            SQLiteDatabase db = this.getReadableDatabase();
            String lim = "";
            if(limit != 0){
                lim = " LIMIT "+String.valueOf(limit);
            }
            if (criteria==null){
                String sql = "select "+columnName+" from "+table_name;
                Cursor cursor =db.rawQuery(sql, null);
                return cursor;
            }else{
                String sql = "select "+columnName+" from "+table_name+" where "+criteriaColum+" = "+criteria+" "+lim;
                Cursor cursor =db.rawQuery(sql, null);
                return cursor;
            }
        }catch (Exception e){
            System.out.println(e);
            return null;
        }

    }

    public boolean updateBalanceData(String table_name, ContentValues contentValues,String columnName,String accountNo){
        try{
            SQLiteDatabase db = this.getWritableDatabase();
            db.update(table_name,contentValues,columnName+" = ?",new String [] {accountNo});
            return true;
        }catch (Exception e){
            return false;
        }

    }
    public Account fetchAccountData(String account_no){
        Account account = new Account();
        try{
            SQLiteDatabase db = this.getReadableDatabase();
            String sql = "select * from "+TABLE_ACCOUNT+ " where accountNo" +" == ?";
            String[] Arg = {account_no};
            Cursor cursor1 =db.rawQuery(sql, Arg);
            if (cursor1.moveToFirst()){
                account.setAccountNo(cursor1.getString(0));
                account.setBankName(cursor1.getString(1));
                account.setAccountHolderName(cursor1.getString(2));
                account.setBalance(cursor1.getDouble(3));
            }
            cursor1.close();
        }catch (Exception e){
            System.out.println(e);
        }
        return account;
    }

}
