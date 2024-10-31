import http from 'k6/http';
import {check, sleep} from 'k6';
import {URL} from 'https://jslib.k6.io/url/1.0.0/index.js';

export const options = {
    stages: [
        {duration: '5m', target: 10},
        {duration: '5m', target: 0},
    ],
};

let flag = false;
let idList = [];

export default function () {
    if (flag === false) {
        flag = true;
        for (let i = 0; i < 10; i++) {
            const username = "hello" + i;
            const joinUrl = new URL(`http://localhost:8080/members`);
            joinUrl.searchParams.append('name', username);
            const joinResponse = http.post(joinUrl.toString());
            idList.push(joinResponse.body);
        }
    }

    const fromUserId = idList[Math.floor(Math.random() * idList.length)];
    let getUserId = idList[Math.floor(Math.random() * idList.length)];
    while (getUserId === fromUserId) {
        getUserId = idList[Math.floor(Math.random() * idList.length)];
    }
    const toUserId = getUserId
    const amount = (Math.floor(Math.random() * 100000) + 100).toString();

    // DEPOSIT
    const depositUrl = new URL(`http://localhost:8080/deposit`);
    depositUrl.searchParams.append('userId', fromUserId);
    depositUrl.searchParams.append('amount', amount);
    const postResponse = http.post(depositUrl.toString());
    check(postResponse, {
        'Deposit status is 200': (r) => r.status === 200,
    });

    // TRANSFER
    const transferUrl = new URL(`http://localhost:8080/transfer`);
    transferUrl.searchParams.append('fromUserId', fromUserId);
    transferUrl.searchParams.append('toUserId', toUserId);
    transferUrl.searchParams.append('amount', amount);
    const transferResponse = http.post(transferUrl.toString());
    check(transferResponse, {
        'Transfer status is 200': (r) => r.status === 200,
    });

    // WITHDRAW
    const withdrawUrl = new URL(`http://localhost:8080/withdraw`);
    withdrawUrl.searchParams.append('userId', toUserId);
    withdrawUrl.searchParams.append('amount', amount);
    const withdrawResponse = http.post(withdrawUrl.toString());
    check(withdrawResponse, {
        'Withdraw status is 200': (r) => r.status === 200,
    });

    sleep(1);
}
