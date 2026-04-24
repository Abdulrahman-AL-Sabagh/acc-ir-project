var a: int[10][10];
var i: int;
var j: int;

fn main() {
    // read some data
    i = 0;
    while (i < 10) {
        j = 0;
        while (j < 10) {
            read a[i][j];
            j = j + 1;
        }
        i = i + 1;
    }

    // add 1 to every value
    i = 0;
    while (i < 10) {
        j = 0;
        while (j < 10) {
            a[i][j] = a[i][j] + 1;
            j = j + 1;
        }
        i = i + 1;
    }
}
