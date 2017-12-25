#!/bin/bash

if [ $# != 2 ] ; then
  echo "Usage: $0 \<fs-multiplay-server-path\> \<authutil.tar-path\>"
  exit 1
fi

fsPath=$1
authUtil=$2

mkdir -p ${fsPath}/etc ${fsPath}/cert ${fsPath}/cfg
cd ${fsPath}
tar xf ${authUtil}
