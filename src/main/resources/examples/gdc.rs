fn main() {
    //println!("hello");
    let n: i32 = gdc(10, 5);
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
