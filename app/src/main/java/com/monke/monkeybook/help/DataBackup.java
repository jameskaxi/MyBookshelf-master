package com.monke.monkeybook.help;

import android.content.Context;
import android.content.SharedPreferences;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.monke.monkeybook.MApplication;
import com.monke.monkeybook.R;
import com.monke.monkeybook.base.observer.SimpleObserver;
import com.monke.monkeybook.bean.BookShelfBean;
import com.monke.monkeybook.bean.BookSourceBean;
import com.monke.monkeybook.bean.ReplaceRuleBean;
import com.monke.monkeybook.bean.SearchHistoryBean;
import com.monke.monkeybook.dao.DbHelper;
import com.monke.monkeybook.model.BookSourceManager;
import com.monke.monkeybook.model.ReplaceRuleManager;
import com.monke.monkeybook.utils.FileUtil;
import com.monke.monkeybook.utils.ToastUtils;

import java.io.File;
import java.util.List;

import androidx.documentfile.provider.DocumentFile;
import io.reactivex.Observable;
import io.reactivex.ObservableOnSubscribe;
import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.schedulers.Schedulers;

/**
 * Created by GKF on 2018/1/30.
 * 数据备份
 */

public class DataBackup {

    public static DataBackup getInstance() {
        return new DataBackup();
    }

    public void run() {
        Observable.create((ObservableOnSubscribe<Boolean>) e -> {
            File dir = FileHelp.getFolder(FileUtil.getSdCardPath(), "YueDu/backups");
            String dirPath = dir.getAbsolutePath();
            backupBookShelf(dirPath);
            backupBookSource(dirPath);
            backupSearchHistory(dirPath);
            backupReplaceRule(dirPath);
            backupConfig(dirPath);
            e.onNext(true);
        })
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(new SimpleObserver<Boolean>() {
                    @Override
                    public void onNext(Boolean value) {
                        if (value) {
                            ToastUtils.longToast(MApplication.getInstance(), R.string.backup_success);
                        } else {
                            ToastUtils.longToast(MApplication.getInstance(), R.string.backup_fail);
                        }
                    }

                    @Override
                    public void onError(Throwable e) {
                        e.printStackTrace();
                        ToastUtils.longToast(MApplication.getInstance(), R.string.backup_fail);
                    }
                });
    }

    private void backupBookShelf(String file) {
        List<BookShelfBean> bookShelfList = BookshelfHelp.queryAllBook();
        if (bookShelfList != null && bookShelfList.size() > 0) {
            for (BookShelfBean bookshelf : bookShelfList) {
                bookshelf.setChapterList(null, false);
            }
            Gson gson = new GsonBuilder()
                    .disableHtmlEscaping()
                    .setPrettyPrinting()
                    .create();
            String bookshelf = gson.toJson(bookShelfList);
            DocumentFile docFile = DocumentHelper.createFileIfNotExist("myBookShelf.json", file);
            DocumentHelper.writeString(bookshelf, docFile);
        }
        BookshelfHelp.queryAllBook();
    }

    private void backupBookSource(String file) {
        List<BookSourceBean> bookSourceList = BookSourceManager.getInstance().getAllBookSource();
        if (bookSourceList != null && bookSourceList.size() > 0) {
            Gson gson = new GsonBuilder()
                    .disableHtmlEscaping()
                    .setPrettyPrinting()
                    .create();
            String str = gson.toJson(bookSourceList);
            DocumentFile docFile = DocumentHelper.createFileIfNotExist("myBookSource.json", file);
            DocumentHelper.writeString(str, docFile);
        }
    }

    private void backupSearchHistory(String file) {
        List<SearchHistoryBean> searchHistoryBeans = DbHelper.getInstance().getDaoSession().getSearchHistoryBeanDao()
                .queryBuilder().list();
        if (searchHistoryBeans != null && searchHistoryBeans.size() > 0) {
            Gson gson = new GsonBuilder()
                    .disableHtmlEscaping()
                    .setPrettyPrinting()
                    .create();
            String str = gson.toJson(searchHistoryBeans);
            DocumentFile docFile = DocumentHelper.createFileIfNotExist("myBookSearchHistory.json", file);
            DocumentHelper.writeString(str, docFile);
        }
    }

    private void backupReplaceRule(String file) {
        List<ReplaceRuleBean> replaceRuleBeans = ReplaceRuleManager.getInstance().getAll();
        if (replaceRuleBeans != null && replaceRuleBeans.size() > 0) {
            Gson gson = new GsonBuilder()
                    .disableHtmlEscaping()
                    .setPrettyPrinting()
                    .create();
            String str = gson.toJson(replaceRuleBeans);
            DocumentFile docFile = DocumentHelper.createFileIfNotExist("myBookReplaceRule.json", file);
            DocumentHelper.writeString(str, docFile);
        }
    }

    private void backupConfig(String file) {
        DocumentFile docFile = DocumentHelper.createFileIfNotExist("config.json", file);
        SharedPreferences pref = MApplication.getInstance().getSharedPreferences("CONFIG", Context.MODE_PRIVATE);
        Gson gson = new GsonBuilder()
                .disableHtmlEscaping()
                .setPrettyPrinting()
                .create();
        String json = gson.toJson(pref.getAll());
        DocumentHelper.writeString(json, docFile);
    }
}
