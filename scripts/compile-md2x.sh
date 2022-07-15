#!/usr/bin/env bash

# Assume current working directory = root of kur-blog repo
cd md2x
npx shadow-cljs release md2x
npx ncc build out/md2x-interim.js -o out --target es5 -m
mv out/index.js out/md2x.js
cd ..