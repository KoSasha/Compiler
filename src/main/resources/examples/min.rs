fn main() {
    let array = [1, 2, 3, 4, -34];
    let min: i32 = min(array);
    println!("{}", min);
}

fn min(array: [i32;5]) -> i32 {
    let mut min: i32 = array[0b0];
    for index in 1..5 {
        if min > array[index] {
            min = array[index];
        }
    }
    return min;
}
.
