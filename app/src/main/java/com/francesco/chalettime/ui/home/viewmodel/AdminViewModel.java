package com.francesco.chalettime.ui.home.viewmodel;

import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.ViewModel;

import com.francesco.chalettime.model.Employee;

import java.util.ArrayList;
import java.util.List;

public class AdminViewModel extends ViewModel {
    private final MutableLiveData<Integer> selectedMonth = new MutableLiveData<>(-1);
    private final MutableLiveData<List<Employee>> employeeList = new MutableLiveData<>(new ArrayList<>());

    public LiveData<Integer> getSelectedMonth() {
        return selectedMonth;
    }

    public void setSelectedMonth(int month) {
        selectedMonth.setValue(month);
    }

    public LiveData<List<Employee>> getEmployeeList() {
        return employeeList;
    }

    public void setEmployeeList(List<Employee> list) {
        employeeList.setValue(list);
    }
}
