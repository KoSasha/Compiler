fn main() {
    let num1: i32 = 11;
    let num2: i32 = 22;
    let n: i32 = gdc(num1, num2);
    let y: i32 = 9;
    let q: i32 = 8;
    println!("{}", q);
    println!("{}", n);

}



fn gdc(mut n: i32, mut m: i32) -> i32 {
    assert!(n != 0);
    while m != 0 {
        if m < n {
            let t = m;
            m = n;
            n = t;
        }
        m = m % n;
    }
    return n;
}
.
