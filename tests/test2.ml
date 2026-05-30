var i: int;
var sum: int;
var a: int[10];

fn main() {
    i = 0;
    while (i < 10) {
        read a[i];
        i = i + 1;
    }

    i = 1;
    sum = 0;
    while (i < 10) {
        sum = sum + (a[i] - a [i - 1]);
        write sum;
        i = i + 1;
    }
    write sum;
}
