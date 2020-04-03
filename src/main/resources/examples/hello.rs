fn main() {
    let s = "popampumoi";
    let subs = "ty";
    let n: i32 = search(s, subs);
    println!("{}", n);
}

fn search(s: &str, subs: &str) -> i32 {
    let mut n: i32 = -1;
    let mut i: i32 = 0;

    for mut index in 0..10 {
        for sindex in 0..2 {
            if s.chars().nth(index) == subs.chars().nth(sindex) {
                n = i;
            } else {
                index = index + 1;
                i = i + 1;
                n = -1;
            }
        }
        i = i + 1;
    }
    return n;
}
.
