package com.alexfoglia.androiddronecontroller;

public interface PicoClient {
    public void onSwVersion(byte major, byte minor, byte stage, byte rel_type);
    public void onThrottleParams(short descend, short hovering, short climb);
    public void onMotorParams(int motorNo, int minSignal, int maxSignal);
    public void onRollPid(float kp, float ki, float kt, float sat, float ad, float bd);
    public void onPitchPid(float kp, float ki, float kt, float sat, float ad, float bd);
    public void onYawPid(float kp, float ki, float kt, float sat, float ad, float bd);

}
