package com.example.rehabilitation.Data;

import android.bluetooth.BluetoothGatt;

public class BleGattService {
    private static BluetoothGatt bleGatt;
    //private BluetoothGatt bleGatt;

    public BleGattService(){
        this.bleGatt=null;
    }

    public  BluetoothGatt getBleGatt(){
        return bleGatt;
    }

    public void setBleGatt(BluetoothGatt bleGatt){
        this.bleGatt=bleGatt;
    }
}
