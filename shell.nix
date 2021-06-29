{ pkgs ? import <nixpkgs> { config.android_sdk.accept_license = true; } }:

pkgs.androidenv.buildApp {
  name = "Xesite";
  platformVersions = [ "29" ];
}
