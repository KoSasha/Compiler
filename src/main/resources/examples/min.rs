fn main() {
    let array = [1, 2, 77, -34, -35, 9];
    let min: i32 = min(array);
    println!("{}", min);
}

fn min(array: [i32;6]) -> i32 {
    let mut min: i32 = array[0b0];
    for index in 1..6 {
        if min > array[index] {
            min = array[index];
        }
    }
    return min;
}
.
